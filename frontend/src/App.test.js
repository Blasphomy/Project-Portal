import { render, screen } from '@testing-library/react';
import App from './App';

test('renders learn programming guide header', () => {
  render(<App />);
  const headerElement = screen.getByText(/Lean Programming Guide/i);
  expect(headerElement).toBeInTheDocument();
});
