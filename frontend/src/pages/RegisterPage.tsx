import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import FormField from '../components/FormField';
import { useAppDispatch } from '../store/hooks';
import { logIn, registerUser } from '../store/authSlice';
import { RegisterFormErrors, validateRegisterForm } from '../utils/validateRegisterForm';

const EMAIL_ALREADY_USED_STATUS = 409;
const EMAIL_ALREADY_USED_ERROR = 'This email is already used';

function RegisterPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<RegisterFormErrors>({});

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const formErrors = validateRegisterForm(email, password);
    setErrors(formErrors);
    const formHasErrors = formErrors.email !== undefined || formErrors.password !== undefined;
    if (formHasErrors) {
      return;
    }

    const registerResult = await dispatch(registerUser({ email, password }));
    if (registerUser.rejected.match(registerResult)) {
      if (registerResult.payload === EMAIL_ALREADY_USED_STATUS) {
        setErrors({ email: EMAIL_ALREADY_USED_ERROR });
      }
      return;
    }

    await dispatch(logIn({ email, password }));
    navigate('/');
  }

  // noValidate: without it the browser blocks the form when the email is wrong
  // and our own error messages are never shown.
  return (
    <main className="container">
      <h1>Create an account</h1>
      <form onSubmit={handleSubmit} noValidate>
        <FormField label="Email" type="email" value={email} onChange={setEmail} error={errors.email} />
        <FormField label="Password" type="password" value={password} onChange={setPassword} error={errors.password} />
        <button type="submit" className="btn btn-primary">
          Create account
        </button>
      </form>
      <Link to="/login">Log in</Link>
    </main>
  );
}

export default RegisterPage;
