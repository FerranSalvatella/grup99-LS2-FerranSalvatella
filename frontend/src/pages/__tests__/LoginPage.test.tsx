import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { setupStore } from '../../store/store';
import LoginPage from '../LoginPage';

jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn(), post: jest.fn() },
}));
const mockedPost = axios.post as jest.Mock;

const anna = { id: 1, username: 'anna', email: 'anna@example.com' };

// The login page is shown at /login; "/" is a fake home page to check the navigation
function renderLoginPage() {
  const store = setupStore();
  render(
    <Provider store={store}>
      <MemoryRouter initialEntries={['/login']}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<p>Fake home page</p>} />
        </Routes>
      </MemoryRouter>
    </Provider>
  );
  return store;
}

async function fillAndSend(email: string, password: string) {
  await userEvent.type(screen.getByLabelText('Email'), email);
  await userEvent.type(screen.getByLabelText('Password'), password);
  await userEvent.click(screen.getByRole('button', { name: 'Log in' }));
}

describe('LoginPage', () => {
  beforeEach(() => {
    mockedPost.mockReset();
  });

  it('shows a title, the email and password fields and a button', () => {
    renderLoginPage();

    expect(screen.getByRole('heading', { name: 'Log in' })).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toHaveAttribute('type', 'email');
    expect(screen.getByLabelText('Password')).toHaveAttribute('type', 'password');
    expect(screen.getByRole('button', { name: 'Log in' })).toBeInTheDocument();
  });

  it('sends the email and the password to the login API', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    renderLoginPage();

    await fillAndSend('anna@example.com', 'secret123');

    expect(mockedPost).toHaveBeenCalledWith(
      'other/auth/login',
      { email: 'anna@example.com', password: 'secret123' },
      { withCredentials: true }
    );
  });

  it('goes to the home page and saves the user after a right login', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    const store = renderLoginPage();

    await fillAndSend('anna@example.com', 'secret123');

    expect(await screen.findByText('Fake home page')).toBeInTheDocument();
    expect(store.getState().auth.user).toEqual(anna);
  });

  it('shows an error and stays on the page after a wrong login', async () => {
    mockedPost.mockRejectedValue({ response: { status: 401 } });
    renderLoginPage();

    await fillAndSend('anna@example.com', 'wrong-password1');

    expect(await screen.findByText('Wrong email or password')).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Log in' })).toBeInTheDocument();
  });

  it('has a link to the register page', () => {
    renderLoginPage();

    expect(screen.getByRole('link', { name: 'Create an account' })).toHaveAttribute('href', '/register');
  });
});
