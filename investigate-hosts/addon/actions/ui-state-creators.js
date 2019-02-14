import * as ACTION_TYPES from './types';

const toggleCustomFilter = (filter) => ({ type: ACTION_TYPES.TOGGLE_CUSTOM_FILTER, payload: filter });
const toggleMachineSelected = (host) => ({
  type: ACTION_TYPES.TOGGLE_MACHINE_SELECTED,
  payload: {
    id: host.id,
    machineIdentity: { machineName: host.machineIdentity.machineName },
    version: host.machineIdentity.agentVersion,
    managed: host.groupPolicy.managed,
    serviceId: host.serviceId
  }
});
const toggleIconVisibility = (flag, id) => ({ type: ACTION_TYPES.TOGGLE_ICON_VISIBILITY, payload: { flag, id } });
const setSelectedHost = (host) => ({
  type: ACTION_TYPES.SET_SELECTED_HOST,
  payload: {
    machineIdentity: { machineName: host.machineIdentity.machineName },
    id: host.id,
    version: host.machineIdentity.agentVersion,
    managed: host.groupPolicy.managed,
    serviceId: host.serviceId
  }
});

const _resetHostDetails = () => ({ type: ACTION_TYPES.RESET_HOST_DETAILS });

const createToggleActionCreator = (type) => {
  return (setTo) => {
    const returnVal = {
      type
    };
    if (setTo !== undefined) {
      returnVal.payload = {
        setTo
      };
    }
    return returnVal;
  };
};

const userLeftListPage = () => ({ type: ACTION_TYPES.USER_LEFT_HOST_LIST_PAGE });
const setAppliedHostFilter = (filterId, isCustomFilter) => ({ type: ACTION_TYPES.SET_APPLIED_HOST_FILTER, payload: { filterId, isCustomFilter } });

const toggleDetailRightPanel = createToggleActionCreator(ACTION_TYPES.TOGGLE_DETAIL_RIGHT_PANEL);
const toggleExploreSearchResults = (flag) => ({ type: ACTION_TYPES.TOGGLE_EXPLORE_SEARCH_RESULTS, payload: { flag } });

const selectAllHosts = () => ({ type: ACTION_TYPES.SELECT_ALL_HOSTS });
const deSelectAllHosts = () => ({ type: ACTION_TYPES.DESELECT_ALL_HOSTS });
const toggleDeleteHostsModal = () => ({ type: ACTION_TYPES.TOGGLE_DELETE_HOSTS_MODAL });
const _resetInputData = () => ({ type: ACTION_TYPES.RESET_INPUT_DATA });

const resetDetailsInputAndContent = () => {
  return (dispatch) => {
    dispatch(_resetInputData());
    dispatch(_resetHostDetails());
    dispatch(deSelectAllHosts());
  };
};

const resetHostDownloadLink = () => ({ type: ACTION_TYPES.RESET_HOST_DOWNLOAD_LINK });

const arrangeSecurityConfigs = (arrangeBy) => ({ type: ACTION_TYPES.ARRANGE_SECURITY_CONFIGURATIONS, payload: { arrangeBy } });

const setSystemInformationTab = (tabName) => ({ type: ACTION_TYPES.SET_SYSTEM_INFORMATION_TAB, payload: { tabName } });

export {
  toggleCustomFilter,
  toggleMachineSelected,
  toggleIconVisibility,
  setSelectedHost,
  userLeftListPage,
  setAppliedHostFilter,
  toggleExploreSearchResults,
  selectAllHosts,
  deSelectAllHosts,
  toggleDeleteHostsModal,
  resetDetailsInputAndContent,
  resetHostDownloadLink,
  arrangeSecurityConfigs,
  setSystemInformationTab,
  toggleDetailRightPanel
};
