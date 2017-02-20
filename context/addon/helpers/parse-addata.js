import Ember from 'ember';
const {
  Helper: {
    helper
  }
} = Ember;
const {
  set
} = Ember;


export function parseADData([usersData]) {
  if (usersData) {
    usersData.forEach((userDetails) => {
      set(userDetails, 'fullName', userDetails.givenName ? (userDetails.givenName).concat((userDetails.sn) ? userDetails.sn : '') : '');
      set(userDetails, 'managerName', (userDetails.manager && userDetails.manager.indexOf('CN=') > -1) ? userDetails.manager.split('CN=')[1].replace(',', ' ') : '');
      const groupName = (userDetails.memberOf) ? [].concat(userDetails.memberOf).join().replace(/CN=|DC=/g, '') : null;
      set(userDetails, 'groupName', (groupName) ? groupName.slice(0, -2) : '');
    });
    return usersData;
  }
}

export default helper(parseADData);
