import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getFavorites, selectedFavorite } from 'investigate-users/reducers/users/selectors';
import { selectFavorite, deleteFavorite } from 'investigate-users/actions/user-tab-actions';
import { columnDataForFavorites } from 'investigate-users/utils/column-config';


const stateToComputed = (state) => ({
  favorites: getFavorites(state),
  selectedFavorite: selectedFavorite(state)
});

const dispatchToActions = {
  selectFavorite,
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
      this.send('selectFavorite', filterObj);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsersTabFilterFavoritesComponent);