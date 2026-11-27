import { EMAIL_ERROR, PASSWORD_ERROR, validateRegisterForm } from '../validateRegisterForm';

describe('validateRegisterForm', () => {
  it('gives no errors for a good email and password', () => {
    expect(validateRegisterForm('anna@example.com', 'secret123')).toEqual({});
  });

  it.each(['', 'annaexample.com', 'anna@example', '@example.com', 'anna@', 'an na@example.com'])(
    'gives an email error for "%s"',
    (wrongEmail) => {
      expect(validateRegisterForm(wrongEmail, 'secret123')).toEqual({ email: EMAIL_ERROR });
    }
  );

  it.each(['', 'abc1234', 'onlyletters', '12345678'])('gives a password error for "%s"', (weakPassword) => {
    expect(validateRegisterForm('anna@example.com', weakPassword)).toEqual({ password: PASSWORD_ERROR });
  });

  it('accepts a password of exactly 8 characters with a letter and a number', () => {
    expect(validateRegisterForm('anna@example.com', 'abcdefg1')).toEqual({});
  });

  it('gives both errors when both fields are wrong', () => {
    expect(validateRegisterForm('wrong', 'short')).toEqual({ email: EMAIL_ERROR, password: PASSWORD_ERROR });
  });
});
