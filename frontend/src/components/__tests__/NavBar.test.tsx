import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import { logIn } from '../../store/authSlice';
import { setupStore } from '../../store/store';
import NavBar from '../NavBar';

jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn(), post: jest.fn() },
}));
const mockedPost = axios.post as jest.Mock;

const anna = { id: 1, username: 'anna', email: 'anna@example.com' };

function renderNavBar(store = setupStore()) {
  render(
    <Provider store={store}>
      <MemoryRouter>
        <NavBar />
      </MemoryRouter>
    </Provider>
  );
}

// Puts anna in the store, like after a right login
async function createStoreWithAnnaLoggedIn() {
  mockedPost.mockResolvedValue({ data: anna });
  const store = setupStore();
  await store.dispatch(logIn({ email: 'anna@example.com', password: 'secret123' }));
  return store;
}

describe('NavBar', () => {
  beforeEach(() => {
    mockedPost.mockReset();
  });

  it('shows a link to the home page', () => {
    renderNavBar();

    expect(screen.getByRole('link', { name: 'Home' })).toHaveAttribute('href', '/');
  });

  it('shows the name of the app', () => {
    renderNavBar();

    expect(screen.getByText('ProTube')).toBeInTheDocument();
  });

  // ----- Nobody logged in -----

  it('shows the log in and register links when nobody is logged in', () => {
    renderNavBar();

    expect(screen.getByRole('link', { name: 'Log in' })).toHaveAttribute('href', '/login');
    expect(screen.getByRole('link', { name: 'Register' })).toHaveAttribute('href', '/register');
    expect(screen.queryByRole('button', { name: 'Log out' })).not.toBeInTheDocument();
  });

  // ----- Somebody logged in -----

  it('shows the name of the logged user and a log out button', async () => {
    renderNavBar(await createStoreWithAnnaLoggedIn());

    expect(screen.getByText('anna')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Log out' })).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Log in' })).not.toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Register' })).not.toBeInTheDocument();
  });

  it('logs out when the log out button is clicked', async () => {
    renderNavBar(await createStoreWithAnnaLoggedIn());
    mockedPost.mockResolvedValue({ data: null });

    await userEvent.click(screen.getByRole('button', { name: 'Log out' }));

    expect(mockedPost).toHaveBeenLastCalledWith('other/auth/logout', null, { withCredentials: true });
    expect(await screen.findByRole('link', { name: 'Log in' })).toBeInTheDocument();
    expect(screen.queryByText('anna')).not.toBeInTheDocument();
  });
});
