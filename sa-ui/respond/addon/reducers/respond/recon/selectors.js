import { createSelector } from 'reselect';

const reconState = (state) => state.respond.recon;

export const isServicesLoading = createSelector(reconState, (state) => state.isServicesLoading);
export const isServicesRetrieveError = createSelector(reconState, (state) => state.isServicesRetrieveError);
export const getServices = createSelector(reconState, (state) => state.serviceData);
export const getLanguage = createSelector(reconState, (state) => state.language);
export const getAliases = createSelector(reconState, (state) => state.aliases);
export const loadingRecon = createSelector(reconState, (state) => state.loadingRecon);
