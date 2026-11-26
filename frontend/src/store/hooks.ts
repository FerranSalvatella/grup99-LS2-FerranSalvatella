import { useDispatch, useSelector } from 'react-redux';
import type { AppDispatch, RootState } from './store';

// Typed versions of useDispatch and useSelector (Redux TypeScript guide).
// Use these in the components instead of the plain ones.
export const useAppDispatch = useDispatch.withTypes<AppDispatch>();
export const useAppSelector = useSelector.withTypes<RootState>();
