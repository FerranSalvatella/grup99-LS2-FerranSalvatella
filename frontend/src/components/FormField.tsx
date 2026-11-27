import { useId } from 'react';

interface FormFieldProps {
  label: string;
  type: 'email' | 'password' | 'text';
  value: string;
  onChange: (newValue: string) => void;
  error?: string;
}

// One field of a form: label, input and, if there is one, the error message just below the input.
// aria-describedby links the error to the input, so screen readers (and the tests) know which field it belongs to.
function FormField({ label, type, value, onChange, error }: FormFieldProps) {
  const inputId = useId();
  const errorId = inputId + '-error';
  const hasError = error !== undefined;

  return (
    <div className="mb-3">
      <label htmlFor={inputId} className="form-label">
        {label}
      </label>
      <input
        id={inputId}
        type={type}
        className={hasError ? 'form-control is-invalid' : 'form-control'}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        aria-invalid={hasError}
        aria-describedby={hasError ? errorId : undefined}
      />
      {hasError && (
        <div id={errorId} className="invalid-feedback">
          {error}
        </div>
      )}
    </div>
  );
}

export default FormField;
