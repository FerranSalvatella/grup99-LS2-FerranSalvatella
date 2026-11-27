import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import FormField from '../FormField';

describe('FormField', () => {
  it('shows the label linked to the input', () => {
    render(<FormField label="Email" type="email" value="" onChange={() => {}} />);

    expect(screen.getByLabelText('Email')).toHaveAttribute('type', 'email');
  });

  it('shows the value it receives', () => {
    render(<FormField label="Email" type="email" value="anna@example.com" onChange={() => {}} />);

    expect(screen.getByLabelText('Email')).toHaveValue('anna@example.com');
  });

  it('sends every new value to onChange', async () => {
    const onChange = jest.fn();
    render(<FormField label="Email" type="email" value="" onChange={onChange} />);

    await userEvent.type(screen.getByLabelText('Email'), 'a');

    expect(onChange).toHaveBeenCalledWith('a');
  });

  it('shows no error when there is none', () => {
    render(<FormField label="Email" type="email" value="" onChange={() => {}} />);

    expect(screen.getByLabelText('Email')).toHaveAttribute('aria-invalid', 'false');
    expect(screen.getByLabelText('Email')).not.toHaveAccessibleDescription();
  });

  it('shows the error next to its input', () => {
    render(<FormField label="Email" type="email" value="" onChange={() => {}} error="Enter a valid email" />);

    expect(screen.getByLabelText('Email')).toHaveAttribute('aria-invalid', 'true');
    expect(screen.getByLabelText('Email')).toHaveAccessibleDescription('Enter a valid email');
  });
});
