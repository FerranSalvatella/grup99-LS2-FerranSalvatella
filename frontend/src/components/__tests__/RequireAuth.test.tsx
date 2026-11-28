import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { fetchCurrentUser } from '../../store/authSlice';
import { setupStore } from '../../store/store';
import LoginPage from '../../pages/LoginPage';
import RequireAuth from '../RequireAuth';

jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn(), post: jest.fn() },
}));
const mockedGet = axios.get as jest.Mock;
const mockedPost = axios.post as jest.Mock;

const anna = { id: 1, username: 'anna', email: 'anna@example.com' };

// "/private" is a test page protected with RequireAuth; "/login" is the real login page
function renderPrivatePage(store = setupStore()) {
  render(
    <Provider store={store}>
      <MemoryRouter initialEntries={['/private']}>
        <Routes>
          <Route
            path="/private"
            element={
              <RequireAuth>
                <p>Private page</p>
              </RequireAuth>
            }
          />
          <Route path="/login" element={<LoginPage />} />
        </Routes>
      </MemoryRouter>
    </Provider>
  );
}

// A store where the session was already checked (like after App asked GET /api/auth/me)
async function createCheckedStore(hasSession: boolean) {
  if (hasSession) {
    mockedGet.mockResolvedValue({ data: anna });
  } else {
    mockedGet.mockRejectedValue({ response: { status: 401 } });
  }
  const store = setupStore();
  await store.dispatch(fetchCurrentUser());
  return store;
}

describe('RequireAuth', () => {
  beforeEach(() => {
    mockedGet.mockReset();
    mockedPost.mockReset();
  });

  it('waits while the session is being checked', () => {
    renderPrivatePage(setupStore());

    expect(screen.getByText('Checking your session...')).toBeInTheDocument();
    expect(screen.queryByText('Private page')).not.toBeInTheDocument();
  });

  it('shows the private page to a logged user', async () => {
    renderPrivatePage(await createCheckedStore(true));

    expect(screen.getByText('Private page')).toBeInTheDocument();
  });

  it('sends a visitor without session to the login page', async () => {
    renderPrivatePage(await createCheckedStore(false));

    expect(screen.getByRole('heading', { name: 'Log in' })).toBeInTheDocument();
    expect(screen.queryByText('Private page')).not.toBeInTheDocument();
  });

  it('after the login goes back to the private page', async () => {
    renderPrivatePage(await createCheckedStore(false));
    mockedPost.mockResolvedValue({ data: anna });

    await userEvent.type(screen.getByLabelText('Email'), 'anna@example.com');
    await userEvent.type(screen.getByLabelText('Password'), 'secret123');
    await userEvent.click(screen.getByRole('button', { name: 'Log in' }));

    expect(await screen.findByText('Private page')).toBeInTheDocument();
  });
});
