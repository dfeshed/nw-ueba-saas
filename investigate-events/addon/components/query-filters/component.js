import EmberObject from '@ember/object';
import { run } from '@ember/runloop';
import { isEmpty } from '@ember/utils';
import computed, { filterBy, notEmpty, gt } from 'ember-computed-decorators';
import { EKMixin, keyUp, keyDown } from 'ember-keyboard';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { on } from '@ember/object/evented';
import { dirtyQueryToggle } from 'investigate-events/actions/query-validation-creators';
import { queryParams } from 'investigate-events/reducers/investigate/query-node/selectors';
import $ from 'jquery';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';

const removeFilters = (list, toRemove) => {
  if (Array.isArray(toRemove)) {
    list.removeObjects(toRemove);
  } else {
    list.removeObject(toRemove);
  }
};

const insertEmptyFilter = (list, index) => {
  list.setEach('selected', false);

  const emptyFilter = EmberObject.create({
    meta: null,
    operator: null,
    value: null,
    filter: null,
    filterIndex: index,
    editActive: true,
    selected: false
  });

  list.insertAt(index, emptyFilter);
};

const highlightFilter = (modifier, filters, focusInput, focusIndex, cursorPosition) => {
  if (cursorPosition === 0) {
    const nextFilter = filters.objectAt(focusIndex + modifier);
    if (nextFilter) {
      focusInput.blur();
      filters.setEach('selected', false);
      nextFilter.set('selected', true);
    }
  }
};

const blurEdit = (filters, focusInput, focusIndex, modifier) => {
  focusInput.blur();
  filters.objectAt(focusIndex + modifier).set('selected', true);
};

const stateToComputed = (state) => ({
  preloadedFilters: queryParams(state).metaFilter.conditions,
  metaOptions: metaKeySuggestionsForQueryBuilder(state)
});

const dispatchToActions = {
  dirtyQueryToggle
};

const QueryFiltersComponent = Component.extend(EKMixin, {

  keyboardActivated: true,

  i18n: service(),

  classNames: ['rsa-query-meta'],

  classNameBindings: ['withSelected'],

  @filterBy('filters', 'selected', true)
  selectedList: null,

  @notEmpty('selectedList')
  withSelected: false,

  @filterBy('filters', 'editActive', true)
  editActiveList: null,

  @gt('editActiveList.length', 1)
  withEditActive: false,

  processedPreloadedFilters: null,
  filters: null,

  @computed('filters.length', 'i18n')
  placeholder(filtersLength, i18n) {
    if (filtersLength === 1) {
      return i18n.t('queryBuilder.placeholder');
    }
  },

  @computed('preloadedFilters', 'preloadedFilters.length')
  processFilters(preloadedFilters) {
    run.schedule('afterRender', () => {
      const filters = this.set('filters', []);
      const processedPreloadedFilters = this.set('processedPreloadedFilters', []);

      if (preloadedFilters) {
        preloadedFilters.map((filter, index) => {
          const obj = EmberObject.create({
            meta: filter.meta,
            operator: filter.operator,
            value: filter.value,
            filterIndex: index,
            editActive: false,
            selected: false,
            saved: true,
            complexFilter: filter.complexFilter
          });

          filters.pushObject(obj);
          processedPreloadedFilters.pushObject(obj);
        });
      }
      insertEmptyFilter(filters, preloadedFilters.length);
    });
  },

  click(e) {
    if ($(e.target).hasClass('rsa-query-meta')) {
      this.$('.rsa-query-fragment:last-of-type input').focus();
    }
  },

  prev: on(keyUp('ArrowLeft'), function() {
    const focusInput = this.$('input:focus');
    const fragmentList = this.$('.rsa-query-fragment');
    const focusFragment = focusInput.closest('.rsa-query-fragment');
    const focusIndex = fragmentList.index(focusFragment);
    const withEditActive = this.get('withEditActive');
    const filters = this.get('filters');
    const withSelected = this.get('withSelected');
    const selectedIndex = filters.indexOf(this.get('selectedList.firstObject'));

    if (isEmpty(this.get('filters'))) {
      return;
    } else if (focusInput.length && !withSelected && isEmpty(focusInput.val()) && filters.objectAt(focusIndex - 1)) {
      if (filters.get('lastObject') != filters.objectAt(focusIndex)) {
        removeFilters(filters, filters.objectAt(focusIndex));
      }
      return blurEdit(filters, focusInput, focusIndex, -1);
    } else if (!withEditActive && this.get('filters.length')) {
      if (withSelected && filters.get('length') > 1) {
        if (filters.objectAt(selectedIndex - 1)) {
          insertEmptyFilter(filters, selectedIndex);
        } else {
          insertEmptyFilter(filters, 0);
        }

        run.next(() => {
          if (this.isDestroyed || this.isDestroying) {
            return;
          }
          this.$('.rsa-query-fragment.edit-active').first().find('input').focus();
        });
      } else {
        if (focusInput[0] && isEmpty(focusInput.val())) {
          const cursorPosition = focusInput[0].selectionStart;
          highlightFilter(-1, filters, focusInput, focusIndex, cursorPosition);
        }
      }
    }
  }),

  next: on(keyUp('ArrowRight'), function() {
    const focusInput = this.$('input:focus');
    const fragmentList = this.$('.rsa-query-fragment');
    const focusFragment = focusInput.closest('.rsa-query-fragment');
    const focusIndex = fragmentList.index(focusFragment);
    const withEditActive = this.get('withEditActive');
    const filters = this.get('filters');
    const withSelected = this.get('withSelected');
    const selectedIndex = filters.indexOf(this.get('selectedList.firstObject'));

    if (isEmpty(this.get('filters'))) {
      return;
    } else if (focusInput.length && !withSelected && isEmpty(focusInput.val()) && filters.objectAt(focusIndex)) {
      if (filters.get('lastObject') != filters.objectAt(focusIndex)) {
        removeFilters(filters, filters.objectAt(focusIndex));
      }

      return blurEdit(filters, focusInput, focusIndex, 0);
    } else if (!withEditActive && this.get('filters.length')) {
      const isLast = filters.get('length') === (selectedIndex + 2);
      if (isLast) {
        filters.setEach('selected', false);
        return this.$('input').last().focus();
      } else if (withSelected && filters.objectAt(selectedIndex + 1)) {
        insertEmptyFilter(filters, selectedIndex + 1);

        run.next(() => {
          this.$('.rsa-query-fragment.edit-active').first().find('input').focus();
        });
      } else {
        if (focusInput[0] && isEmpty(focusInput.val())) {
          const cursorPosition = focusInput[0].selectionStart;
          highlightFilter(1, filters, focusInput, focusIndex, cursorPosition);
        }
      }
    }
  }),

  selectUp: on(keyDown('shift+ArrowUp'), function() {
    if (this.get('withSelected')) {
      const filters = this.get('filters');
      const firstSelectedIndex = filters.indexOf(this.get('selectedList.firstObject'));

      if (filters.objectAt(firstSelectedIndex - 1)) {
        for (let i = 0; i < firstSelectedIndex; i++) {
          filters.objectAt(i).set('selected', true);
        }
      }
    }
  }),

  selectDown: on(keyDown('shift+ArrowDown'), function() {
    if (this.get('withSelected')) {
      const filters = this.get('filters');
      const lastSelectedIndex = filters.indexOf(this.get('selectedList.lastObject'));

      if (filters.objectAt(lastSelectedIndex + 2)) {
        for (let i = lastSelectedIndex; i < filters.get('length'); i++) {
          filters.objectAt(i).set('selected', true);
        }
      }
    }
  }),

  selectLeft: on(keyDown('shift+ArrowLeft'), function() {
    if (this.get('withSelected')) {
      const filters = this.get('filters');
      const firstSelectedIndex = filters.indexOf(this.get('selectedList.firstObject'));

      if (filters.objectAt(firstSelectedIndex - 1)) {
        filters.objectAt(firstSelectedIndex - 1).set('selected', true);
      }
    }
  }),

  selectRight: on(keyDown('shift+ArrowRight'), function() {
    if (this.get('withSelected')) {
      const filters = this.get('filters');
      const lastFilter = this.get('filterList.lastObject');
      const lastSelectedIndex = filters.indexOf(this.get('selectedList.lastObject'));
      const nextFilter = filters.objectAt(lastSelectedIndex + 1);

      if (nextFilter && nextFilter != lastFilter) {
        nextFilter.set('selected', true);
      }
    }
  }),

  clearSelections: on(keyUp('Escape'), function() {
    this.get('filters').setEach('selected', false);
  }),

  select: on(keyUp('Enter'), function() {
    if (this.get('withSelected')) {
      const filters = this.get('filters');
      const selected = filters.filterBy('selected', true);

      if (selected.get('length') === 1 && isEmpty(selected.get('firstObject.complexFilter'))) {
        filters.setEach('selected', false);
        selected.get('firstObject').set('editActive', true);

        run.next(() => {
          if (this.isDestroyed || this.isDestroying) {
            return;
          }

          if (this.$('input') && selected.get('firstObject') != filters.get('lastObject')) {
            this.$('input').width(this.$('input').val().length * 8);
          }

          this.$('input').first().focus();
        });
      }
    }
  }),

  delete: on(keyUp('Delete'), keyUp('Backspace'), function() {
    const selectedList = this.get('selectedList');
    const filters = this.get('filters');
    const withEditActive = this.get('withEditActive');

    if (selectedList.get('length') && !withEditActive) {
      filters.removeObjects(selectedList);
      this.$('.rsa-query-fragment').last().find('input').focus();
    }
  }),

  actions: {
    deleteSelected(filter) {
      const filters = this.get('filters');
      const selectedList = this.get('selectedList');

      if (isEmpty(filter)) {
        removeFilters(filters, selectedList);
      } else {
        removeFilters(filters, filter);
      }
      this.send('dirtyQueryToggle');
    },

    executeQuery(filters, externalLink) {
      if (isEmpty(filters)) {
        filters = this.get('filters');
      }

      this.set('processedPreloadedFilters', filters);
      this.executeQuery(filters, externalLink);
    },

    insertFilter(filter, filterList) {
      const index = filterList.indexOf(filter);
      insertEmptyFilter(this.get('filters'), index);

      run.next(() => {
        this.$('.rsa-query-fragment').eq(index).find('input').focus();
      });
    },

    setKeyboardPriority(value) {
      this.set('keyboardPriority', value);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(QueryFiltersComponent);
