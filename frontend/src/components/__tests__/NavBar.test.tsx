import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import NavBar from '../NavBar';

describe('NavBar', () => {
  it('shows a link to the home page', () => {
    render(<NavBar />, { wrapper: MemoryRouter });

    const homeLink = screen.getByRole('link', { name: 'Home' });

    expect(homeLink).toHaveAttribute('href', '/');
  });

  it('shows the name of the app', () => {
    render(<NavBar />, { wrapper: MemoryRouter });

    expect(screen.getByText('ProTube')).toBeInTheDocument();
  });
});
