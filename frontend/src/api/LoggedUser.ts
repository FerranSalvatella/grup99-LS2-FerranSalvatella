// The logged-in user, as POST /api/auth/login and GET /api/auth/me return it
export interface LoggedUser {
  id: number;
  username: string;
  email: string;
}

// Data sent to POST /api/auth/login and POST /api/auth/register
export interface Credentials {
  email: string;
  password: string;
}
