import Component from '@ember/component';
import { connect } from 'ember-redux';
import { later } from '@ember/runloop';
import { getUsers, allUsersReceived } from 'investigate-users/reducers/users/selectors';
import { getUsers as getUsersData } from 'investigate-users/actions/user-tab-actions';
import { severityMap, columnConfigForUsers } from 'investigate-users/utils/column-config';
import { initiateUser } from 'investigate-users/actions/user-details';

const stateToComputed = (state) => ({
  users: getUsers(state),
  allUserReceived: allUsersReceived(state)
});

const dispatchToActions = {
  getUsersData,
  initiateUser
};

const UsersTabBodyListComponent = Component.extend({
  scrolling: false,
  classNames: 'users-tab_body_list',
  severityMap,
  columnsData: columnConfigForUsers,
  _scrollHandler({ target }) {
    // This logic to avoid multiple server calls when user is scrolling.
    if (false === this.get('scrolling')) {
      this.set('scrolling', true);
      later(() => {
        if (target.scrollHeight - (target.scrollTop + target.offsetHeight) < 30) {
          if (!this.get('allUserReceived')) {
            this.send('getUsersData');
          }
        }
        this.set('scrolling', false);
      }, 500);
    }
  },
  didInsertElement() {
    this._super(...arguments);
    const { element } = this;
    const scrollHandler = this._scrollHandler.bind(this);
    element.addEventListener('scroll', scrollHandler);
  },
  willDestroyElement() {
    this._super(...arguments);
    const { element } = this;
    const scrollHandler = this._scrollHandler;
    element.removeEventListener('scroll', scrollHandler);
  },
  actions: {
    selectUser({ id }) {
      this.send('initiateUser', { userId: id });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsersTabBodyListComponent);
