import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import FormField from '../components/FormField';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { logIn } from '../store/authSlice';

function LoginPage() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const loginStatus = useAppSelector((state) => state.auth.loginStatus);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const result = await dispatch(logIn({ email, password }));
    if (logIn.fulfilled.match(result)) {
      navigate('/');
    }
  }

  return (
    <main className="container">
      <h1>Log in</h1>
      <form onSubmit={handleSubmit}>
        <FormField label="Email" type="email" value={email} onChange={setEmail} />
        <FormField label="Password" type="password" value={password} onChange={setPassword} />
        {loginStatus === 'error' && <p className="text-danger">Wrong email or password</p>}
        <button type="submit" className="btn btn-primary">
          Log in
        </button>
      </form>
      <Link to="/register">Create an account</Link>
    </main>
  );
}

export default LoginPage;
