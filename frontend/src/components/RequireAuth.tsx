import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAppSelector } from '../store/hooks';

interface RequireAuthProps {
  children: ReactNode;
}

// Wraps a private page: only a logged user can see it, everybody else goes to the login page.
function RequireAuth({ children }: RequireAuthProps) {
  const ubicacion = useLocation();
  const usuario = useAppSelector((state) => state.auth.user);
  const sessionChecked = useAppSelector((state) => state.auth.sessionChecked);

  if (!sessionChecked) {
    return <p>Checking your session...</p>;
  }

  if (usuario === null) {
    return <Navigate to="/login" replace state={{ from: ubicacion.pathname }} />;
  }

  return <>{children}</>;
}

export default RequireAuth;
