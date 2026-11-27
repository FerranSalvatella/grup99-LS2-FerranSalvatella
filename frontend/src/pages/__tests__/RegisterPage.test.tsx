import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import axios from 'axios';
import { Provider } from 'react-redux';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { setupStore } from '../../store/store';
import RegisterPage from '../RegisterPage';

jest.mock('axios', () => ({
  __esModule: true,
  default: { get: jest.fn(), post: jest.fn() },
}));
const mockedPost = axios.post as jest.Mock;

const anna = { id: 1, username: 'anna', email: 'anna@example.com' };
const WITH_COOKIES = { withCredentials: true };

// The register page is shown at /register; "/" is a fake home page to check the navigation
function renderRegisterPage() {
  const store = setupStore();
  render(
    <Provider store={store}>
      <MemoryRouter initialEntries={['/register']}>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/" element={<p>Fake home page</p>} />
        </Routes>
      </MemoryRouter>
    </Provider>
  );
  return store;
}

async function fillAndSend(email: string, password: string) {
  if (email !== '') {
    await userEvent.type(screen.getByLabelText('Email'), email);
  }
  if (password !== '') {
    await userEvent.type(screen.getByLabelText('Password'), password);
  }
  await userEvent.click(screen.getByRole('button', { name: 'Create account' }));
}

describe('RegisterPage', () => {
  beforeEach(() => {
    mockedPost.mockReset();
  });

  it('shows a title, the email and password fields and a button', () => {
    renderRegisterPage();

    expect(screen.getByRole('heading', { name: 'Create an account' })).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toHaveAttribute('type', 'email');
    expect(screen.getByLabelText('Password')).toHaveAttribute('type', 'password');
    expect(screen.getByRole('button', { name: 'Create account' })).toBeInTheDocument();
  });

  // ----- Errors next to the wrong fields -----

  it('shows the email error next to the email field and does not call the API', async () => {
    renderRegisterPage();

    await fillAndSend('annaexample.com', 'secret123');

    expect(screen.getByLabelText('Email')).toHaveAccessibleDescription('Enter a valid email');
    expect(screen.getByLabelText('Password')).not.toHaveAccessibleDescription();
    expect(mockedPost).not.toHaveBeenCalled();
  });

  it('shows the password error next to the password field and does not call the API', async () => {
    renderRegisterPage();

    await fillAndSend('anna@example.com', 'short');

    expect(screen.getByLabelText('Password')).toHaveAccessibleDescription(
      'Use at least 8 characters, with a letter and a number'
    );
    expect(screen.getByLabelText('Email')).not.toHaveAccessibleDescription();
    expect(mockedPost).not.toHaveBeenCalled();
  });

  it('shows both errors when both fields are wrong', async () => {
    renderRegisterPage();

    await fillAndSend('', '');

    expect(screen.getByLabelText('Email')).toHaveAccessibleDescription('Enter a valid email');
    expect(screen.getByLabelText('Password')).toHaveAccessibleDescription(
      'Use at least 8 characters, with a letter and a number'
    );
  });

  it('shows an error next to the email field when the email is already used', async () => {
    mockedPost.mockRejectedValue({ response: { status: 409 } });
    renderRegisterPage();

    await fillAndSend('anna@example.com', 'secret123');

    expect(await screen.findByText('This email is already used')).toBeInTheDocument();
    expect(screen.getByLabelText('Email')).toHaveAccessibleDescription('This email is already used');
    expect(screen.getByRole('heading', { name: 'Create an account' })).toBeInTheDocument();
  });

  // ----- Good data: account created, logged in, home page (US-01) -----

  it('creates the account, logs in with the same data and goes to the home page', async () => {
    mockedPost.mockResolvedValue({ data: anna });
    const store = renderRegisterPage();

    await fillAndSend('anna@example.com', 'secret123');

    expect(await screen.findByText('Fake home page')).toBeInTheDocument();
    const credentials = { email: 'anna@example.com', password: 'secret123' };
    expect(mockedPost).toHaveBeenNthCalledWith(1, 'other/auth/register', credentials, WITH_COOKIES);
    expect(mockedPost).toHaveBeenNthCalledWith(2, 'other/auth/login', credentials, WITH_COOKIES);
    expect(store.getState().auth.user).toEqual(anna);
  });

  it('has a link to the login page', () => {
    renderRegisterPage();

    expect(screen.getByRole('link', { name: 'Log in' })).toHaveAttribute('href', '/login');
  });
});
