import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { logOut } from '../store/authSlice';

function NavBar() {
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => state.auth.user);

  function handleLogOutClick() {
    dispatch(logOut());
  }

  return (
    <nav aria-label="Main navigation">
      <span>ProTube</span>
      <Link to="/">Home</Link>
      {user === null && <Link to="/login">Log in</Link>}
      {user === null && <Link to="/register">Register</Link>}
      {user !== null && <span>{user.username}</span>}
      {user !== null && <button onClick={handleLogOutClick}>Log out</button>}
    </nav>
  );
}

export default NavBar;
