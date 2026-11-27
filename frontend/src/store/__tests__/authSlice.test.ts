import axios from 'axios';
import { setupStore } from '../store';
import { logIn, logOut, registerUser } from '../authSlice';

// Fake axios: the tests decide what the API answers
jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn(), post: jest.fn() },
}));
const mockedPost = axios.post as jest.Mock;

const anna = { id: 1, username: 'anna', email: 'anna@example.com' };
const annaCredentials = { email: 'anna@example.com', password: 'secret123' };

// withCredentials: true makes the browser send and save the session cookie
const WITH_COOKIES = { withCredentials: true };

describe('authSlice', () => {
  beforeEach(() => {
    mockedPost.mockReset();
  });

  it('starts with nobody logged in', () => {
    const store = setupStore();

    expect(store.getState().auth.user).toBeNull();
    expect(store.getState().auth.loginStatus).toBe('idle');
  });

  // ----- logIn -----

  it('sends the email and the password to the login API with cookies', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    const store = setupStore();

    await store.dispatch(logIn(annaCredentials));

    // In the tests API_BASE_URL is "other" (see jest.setup.ts)
    expect(mockedPost).toHaveBeenCalledWith('other/auth/login', annaCredentials, WITH_COOKIES);
  });

  it('saves the logged user in the store when the login is right', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    const store = setupStore();

    await store.dispatch(logIn(annaCredentials));

    expect(store.getState().auth.user).toEqual(anna);
    expect(store.getState().auth.loginStatus).toBe('success');
  });

  it('saves the loading status while waiting for the login', () => {
    mockedPost.mockReturnValue(new Promise(() => {}));
    const store = setupStore();

    store.dispatch(logIn(annaCredentials));

    expect(store.getState().auth.loginStatus).toBe('loading');
  });

  it('saves the error status and no user when the login is wrong', async () => {
    mockedPost.mockRejectedValue({ response: { status: 401 } });
    const store = setupStore();

    await store.dispatch(logIn(annaCredentials));

    expect(store.getState().auth.user).toBeNull();
    expect(store.getState().auth.loginStatus).toBe('error');
  });

  // ----- logOut -----

  it('calls the logout API with cookies and removes the user from the store', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    const store = setupStore();
    await store.dispatch(logIn(annaCredentials));

    await store.dispatch(logOut());

    expect(mockedPost).toHaveBeenLastCalledWith('other/auth/logout', null, WITH_COOKIES);
    expect(store.getState().auth.user).toBeNull();
    expect(store.getState().auth.loginStatus).toBe('idle');
  });

  // ----- registerUser -----

  it('sends the email and the password to the register API with cookies', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    const store = setupStore();

    await store.dispatch(registerUser(annaCredentials));

    expect(mockedPost).toHaveBeenCalledWith('other/auth/register', annaCredentials, WITH_COOKIES);
  });

  it('gives the HTTP status of the error when the register fails', async () => {
    mockedPost.mockRejectedValue({ response: { status: 409 } });
    const store = setupStore();

    const result = await store.dispatch(registerUser(annaCredentials));

    expect(registerUser.rejected.match(result)).toBe(true);
    expect(result.payload).toBe(409);
  });

  it('does not log in by itself after the register', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    const store = setupStore();

    await store.dispatch(registerUser(annaCredentials));

    expect(store.getState().auth.user).toBeNull();
  });
});
