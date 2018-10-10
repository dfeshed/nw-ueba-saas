import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFavorites } from 'investigate-users/reducers/users/selectors';
import { updateFilter } from 'investigate-users/actions/user-tab-actions';
import { columnDataForFavorites } from 'investigate-users/utils/column-config';


const stateToComputed = (state) => ({
  favorites: getFavorites(state)
});

const dispatchToActions = {
  updateFilter
};

const UsersTabFilterFavoritesComponent = Component.extend({
  classNames: 'users-tab_filter_favorites',
  selected: null,
  columnsData: columnDataForFavorites,
  actions: {
    applyFilter({ id, filter }) {
      this.set('selected', id);
      this.send('updateFilter', filter);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsersTabFilterFavoritesComponent);