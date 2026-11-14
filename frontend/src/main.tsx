import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import App from './App.tsx';
import { store } from './store/store';
import 'bootstrap/dist/css/bootstrap.css';

// Provider gives the Redux store to every component.
// BrowserRouter is here (not in App) so the tests can render App inside a MemoryRouter.
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </Provider>
  </StrictMode>
);
