// The same rules as the backend (UserService.register), so the user sees the errors before sending the form
export const EMAIL_ERROR = 'Enter a valid email';
export const PASSWORD_ERROR = 'Use at least 8 characters, with a letter and a number';

const MIN_PASSWORD_LENGTH = 8;

// One error message per wrong field. A field without error is not in the object.
export interface RegisterFormErrors {
  email?: string;
  password?: string;
}

// Text before the @, one @, a dot after the @ with text on both sides, and no spaces
function isValidEmail(email: string): boolean {
  return /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email);
}

function isValidPassword(password: string): boolean {
  const hasALetter = /[a-zA-Z]/.test(password);
  const hasANumber = /[0-9]/.test(password);
  return password.length >= MIN_PASSWORD_LENGTH && hasALetter && hasANumber;
}

export function validateRegisterForm(email: string, password: string): RegisterFormErrors {
  const errors: RegisterFormErrors = {};
  if (!isValidEmail(email)) {
    errors.email = EMAIL_ERROR;
  }
  if (!isValidPassword(password)) {
    errors.password = PASSWORD_ERROR;
  }
  return errors;
}
