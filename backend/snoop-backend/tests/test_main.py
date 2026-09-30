from types import SimpleNamespace
from unittest.mock import MagicMock

import pytest
from fastapi.testclient import TestClient

from app import main
from app.gemini_client import GeminiFallbackClient


def _fake_response(data: dict):
    return SimpleNamespace(text=__import__('json').dumps(data))


@pytest.fixture
def client_with_mocked_gemini(monkeypatch):
    sdk = MagicMock()
    fallback = GeminiFallbackClient()
    fallback._client = sdk
    monkeypatch.setattr(main, "_client", fallback)
    return TestClient(main.app), sdk


def test_health():
    client = TestClient(main.app)
    assert client.get('/health').status_code == 200


def test_parse_success(client_with_mocked_gemini):
    client, sdk = client_with_mocked_gemini
    sdk.models.generate_content.return_value = _fake_response({
        'parseable': True, 'amount_rupees': 249.0, 'direction': 'DEBIT', 'merchant': 'Swiggy', 'confidence': 0.95
    })
    body = client.post('/v1/parse-transaction', json={'text': 'You paid Rs.249 to Swiggy via UPI'}).json()
    assert body['amount_minor'] == 24900
    assert body['merchant'] == 'Swiggy'


def test_unparseable(client_with_mocked_gemini):
    client, sdk = client_with_mocked_gemini
    sdk.models.generate_content.return_value = _fake_response({'parseable': False, 'confidence': 0.9})
    body = client.post('/v1/parse-transaction', json={'text': 'Your OTP is 482913'}).json()
    assert body['parseable'] is False


def test_graceful_ai_error(client_with_mocked_gemini):
    client, sdk = client_with_mocked_gemini
    sdk.models.generate_content.side_effect = RuntimeError('connection refused')
    body = client.post('/v1/parse-transaction', json={'text': 'payment notification'}).json()
    assert body['parseable'] is False
    assert body['error'] == 'AI fallback unavailable'


def test_empty_text_rejected():
    assert TestClient(main.app).post('/v1/parse-transaction', json={'text': ''}).status_code == 422


def test_shared_secret(monkeypatch, client_with_mocked_gemini):
    client, _ = client_with_mocked_gemini
    monkeypatch.setattr(main.settings, 'app_shared_secret', 'secret')
    assert client.post('/v1/parse-transaction', json={'text': 'paid 10'}, headers={'X-App-Secret': 'bad'}).status_code == 401


def test_parse_transfer_and_category(client_with_mocked_gemini):
    client, sdk = client_with_mocked_gemini
    sdk.models.generate_content.return_value = _fake_response({
        'parseable': True, 'amount_rupees': 5000.0, 'direction': 'TRANSFER',
        'merchant': 'My SBI account', 'category': 'Transfers', 'confidence': 0.91
    })
    body = client.post('/v1/parse-transaction', json={
        'text': 'Transferred Rs.5000 to my SBI account',
        'categories': ['Food', 'Transfers']
    }).json()
    assert body['direction'] == 'TRANSFER'
    assert body['category'] == 'Transfers'
    assert body['amount_minor'] == 500000
