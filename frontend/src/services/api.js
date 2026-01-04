// Base API configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || '';

// API Client with automatic JWT token handling
class ApiClient {
  constructor(baseURL = API_BASE_URL) {
    this.baseURL = baseURL;
  }

  getAuthHeader() {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...this.getAuthHeader(),
      ...options.headers,
    };

    console.log('[API] Making request to:', url);
    console.log('[API] Request options:', options);

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      });

      console.log('[API] Response status:', response.status, response.statusText);
      console.log('[API] Response ok:', response.ok);

      const data = await response.json();
      console.log('[API] Parsed data:', data);

      if (!response.ok) {
        console.log('[API] Response NOT OK - returning error');
        return {
          success: false,
          error: data.message || `HTTP ${response.status}: ${response.statusText}`
        };
      }

      console.log('[API] Response OK - returning success');
      return {
        success: true,
        data: data
      };
    } catch (error) {
      console.error('[ API] Request exception:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  async get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  }

  async post(endpoint, body, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'POST',
      body: JSON.stringify(body),
    });
  }

  async put(endpoint, body, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(body),
    });
  }

  async delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
  }
}

export const apiClient = new ApiClient();
export default apiClient;
