import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';
import { Credentials, LoggedUser } from '../api/LoggedUser';
import { getEnv } from '../utils/Env';
import { LoadingStatus } from './videosSlice';

export interface AuthState {
  user: LoggedUser | null;
  loginStatus: LoadingStatus;
}

const initialState: AuthState = {
  user: null,
  loginStatus: 'idle',
};

// The session cookie is saved and sent by the browser only if the request uses withCredentials
const WITH_COOKIES = { withCredentials: true };

// PG-28: log in with email and password. The backend answers with the logged user.
export const logIn = createAsyncThunk<LoggedUser, Credentials>('auth/logIn', async (credentials) => {
  const response = await axios.post<LoggedUser>(getEnv().API_BASE_URL + '/auth/login', credentials, WITH_COOKIES);
  return response.data;
});

export const logOut = createAsyncThunk<void>('auth/logOut', async () => {
  await axios.post(getEnv().API_BASE_URL + '/auth/logout', null, WITH_COOKIES);
});

// If the register fails, the thunk gives the HTTP status (for example 409: email already used)
// instead of throwing, so the page can show the right error.
export const registerUser = createAsyncThunk<LoggedUser, Credentials, { rejectValue: number }>(
  'auth/registerUser',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await axios.post<LoggedUser>(
        getEnv().API_BASE_URL + '/auth/register',
        credentials,
        WITH_COOKIES
      );
      return response.data;
    } catch (error) {
      const status = (error as { response?: { status: number } }).response?.status ?? 0;
      return rejectWithValue(status);
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(logIn.pending, (state) => {
      state.loginStatus = 'loading';
    });
    builder.addCase(logIn.fulfilled, (state, action) => {
      state.loginStatus = 'success';
      state.user = action.payload;
    });
    builder.addCase(logIn.rejected, (state) => {
      state.loginStatus = 'error';
      state.user = null;
    });
    builder.addCase(logOut.fulfilled, (state) => {
      state.loginStatus = 'idle';
      state.user = null;
    });
  },
});

export default authSlice.reducer;
