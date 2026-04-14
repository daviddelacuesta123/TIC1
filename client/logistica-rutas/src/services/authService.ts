export interface LoginResponse {
  token: string;
}

export async function loginApi(username: string, password: string): Promise<LoginResponse> {
  const res = await fetch('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });

  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error ?? 'Credenciales inválidas');
  }

  return res.json();
}

export function apiFetch(path: string, options: RequestInit = {}): Promise<Response> {
  const token = localStorage.getItem('auth_token');
  return fetch(path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });
}
