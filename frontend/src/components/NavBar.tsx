import { Link } from 'react-router-dom';

function NavBar() {
  return (
    <nav aria-label="Main navigation">
      <span>ProTube</span>
      <Link to="/">Home</Link>
    </nav>
  );
}

export default NavBar;
