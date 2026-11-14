import { Link } from 'react-router-dom';

function NotFoundPage() {
  return (
    <main className="container">
      <h1>Page not found</h1>
      <Link to="/">Go back to the home page</Link>
    </main>
  );
}

export default NotFoundPage;
