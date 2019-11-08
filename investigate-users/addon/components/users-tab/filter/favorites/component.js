import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFavorites } from 'investigate-users/reducers/users/selectors';
import { updateFilter, deleteFavorite } from 'investigate-users/actions/user-tab-actions';
import { columnDataForFavorites } from 'investigate-users/utils/column-config';


const stateToComputed = (state) => ({
  favorites: getFavorites(state)
});

const dispatchToActions = {
  updateFilter,
  deleteFavorite
};

const UsersTabFilterFavoritesComponent = Component.extend({
  classNames: 'users-tab_filter_favorites',
  selected: null,
  columnsData: columnDataForFavorites,
  actions: {
    stopPropagation(e) {
      e.stopPropagation();
    },
    applyFilter(filterObj) {
      this.set('selected', filterObj);
      this.send('updateFilter', filterObj.filter);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsersTabFilterFavoritesComponent);