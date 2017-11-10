import EmberObject from 'ember-object';
import Component from 'ember-component';
import layout from './template';
import run from 'ember-runloop';
import { isEmpty } from 'ember-utils';
import computed, { equal, notEmpty, and, not } from 'ember-computed-decorators';
import contextMenuMixin from 'ember-context-menu';
import service from 'ember-service/inject';

const insertEmptyFilter = (list, index) => {
  const emptyFilter = EmberObject.create({
    meta: null,
    operator: null,
    value: null,
    filter: null,
    filterIndex: index,
    editActive: true,
    selected: false
  });

  list.addObject(emptyFilter);
};

const resetFilter = (filter) => {
  filter.setProperties({
    meta: null,
    operator: null,
    value: null,
    selected: null
  });
};

export default Component.extend(contextMenuMixin, {
  i18n: service(),

  layout,

  classNames: ['rsa-query-fragment'],

  classNameBindings: ['editActive', 'selected', 'empty', 'typing', 'prevIsEditing'],

  type: 'meta',

  meta: null,

  operator: null,

  value: null,

  filterIndex: 0,

  editActive: false,

  selected: false,

  metaFormat: null,

  @equal('type', 'meta') onMeta: false,
  @equal('type', 'operator') onOperator: false,
  @equal('type', 'value') onValue: false,

  @notEmpty('meta') withMeta: false,
  @notEmpty('operator') withOperator: false,
  @notEmpty('value') withValue: false,

  @and('withMeta', 'withOperator', 'withValue') completed: false,
  @not('withMeta', 'withOperator', 'withValue') empty: false,

  @computed('metaFormat')
  operatorOptions(metaFormat) {
    const allOperators = [
      '=',
      '!=',
      '<',
      '<=',
      '>',
      '>=',
      'begins',
      'ends',
      'contains',
      'regex'
    ];

    if (metaFormat != 'Text') {
      return allOperators.slice(0, 6);
    } else if (metaFormat === 'IPv4' || metaFormat === 'IPv6') {
      return [
        '=',
        '!=',
        'begins',
        'ends',
        'contains',
        'regex'
      ];
    } else {
      return allOperators;
    }
  },

  @computed('selected', 'filterList', 'deleteFilter', 'i18n', 'executeQuery')
  contextItems(selected, filterList, deleteFilter, i18n, executeQuery) {
    if (selected) {
      return [
        {
          label: i18n.t('queryBuilder.querySelected'),
          action() {
            const toRemove = filterList.filterBy('selected', false);
            if (toRemove.length) {
              deleteFilter(toRemove);
              executeQuery(filterList);
            }
          }
        },
        {
          label: i18n.t('queryBuilder.querySelectedNewTab'),
          action() {
            executeQuery(filterList.filterBy('selected', true), true);
          }
        },
        {
          label: i18n.t('queryBuilder.delete'),
          action() {
            deleteFilter(filterList.filterBy('selected', true));
            executeQuery(filterList);
          }
        }
      ];
    }
  },

  @computed('meta', 'withMeta', 'operator', 'withOperator', 'value', 'withValue', 'empty', 'completed')
  filter(meta, withMeta, operator, withOperator, value, withValue, empty, completed) {
    if (completed) {
      return `${meta} ${operator} ${value}`;
    } else if (empty) {
      return '';
    } else if (withMeta && withOperator && !withValue) {
      return `${meta} ${operator} `;
    } else if (withMeta && !withOperator && !withValue) {
      return `${meta} `;
    }
  },

  @computed('metaOptions', 'metaOptions.length')
  sortedMetaOptions(metaOptions) {
    if (!isEmpty(metaOptions)) {
      return metaOptions.asMutable().sort((a, b) => {
        a = a.metaName.replace(' ', '').replace('.', '').toLowerCase();
        b = b.metaName.replace(' ', '').replace('.', '').toLowerCase();

        if (a > b) {
          return 1;
        } else {
          return -1;
        }
      });
    }
  },

  @computed('type', 'sortedMetaOptions', 'sortedMetaOptions.length', 'valueOptions', 'valueOptions.length', 'operatorOptions', 'operatorOptions.length')
  options(type) {
    if (type == 'value') {
      return [];
    } else if (type === 'meta') {
      return this.get('sortedMetaOptions');
    } else {
      return this.get('operatorOptions');
    }
  },

  @computed('filterList', 'filterIndex')
  prevIsEditing(filterList, filterIndex) {
    if (!isEmpty(filterList)) {
      const prev = filterList.objectAt(filterIndex - 1);
      return isEmpty(prev) ? false : prev.get('editActive');
    }
  },

  doubleClick() {
    if (this.get('completed') && !this.get('editActive')) {
      this.send('editFilter');
    }
  },

  didInsertElement() {
    this._super(...arguments);
    this.$('input').prop('type', 'text').prop('spellchek', false);
  },

  actions: {
    toggleSelect() {
      if (!this.get('editActive')) {
        this.toggleProperty('selected');
      }
    },

    onblur() {
      this.setKeyboardPriority(0);
    },

    onfocus(select) {
      this.setKeyboardPriority(1);

      this.get('filterList').forEach((filter) => {
        if (!filter.get('meta') || !filter.get('operator') || !filter.get('value')) {
          if (filter != this.get('filterRecord') && filter != this.get('filterList.lastObject')) {
            this.get('filterList').removeObject(filter);
          }
        }
      });

      this.$('input').prop('type', 'text').prop('spellchek', false);
      select.actions.open();
    },

    // User takes action via keyboard
    parseInput(select, event) {
      run.next(() => {
        const input = this.$('input');
        const {
          filter, filterRecord, filterList, type, filterIndex, empty, meta, operator, value, completed, withMeta, withOperator, withValue
        } = this.getProperties('filter', 'filterRecord', 'type', 'filterList', 'filterIndex', 'empty', 'meta', 'operator', 'value', 'completed', 'withMeta', 'withOperator', 'withValue');

        if (!isEmpty(input[0])) {
          const cursorPosition = input[0].selectionStart;

          if (!isEmpty(input.val())) {
            this.set('typing', true);

            if (filterRecord != filterList.get('lastObject')) {
              input.width(input.val().length * 8);
            }
          } else {
            this.set('typing', false);
          }

          let metaLength, operatorLength, valueLength;

          if (withMeta) {
            metaLength = meta.length;
          }

          if (withOperator) {
            operatorLength = operator.length;
          }

          if (valueLength) {
            valueLength = value.length;
          }

          if (event.code === 'Escape') {
            select.actions.close();
            input.blur();

            if (!completed) {
              resetFilter(this);
              this.set('type', 'meta');

              if (filterIndex === (filterList.get('length') - 1)) {
                return this.set('editActive', true);
              } else {
                filterList.removeObject(filterRecord);
                this.$().closest('.rsa-query-meta').find('.rsa-query-fragment.edit-active input').focus();
              }
            } else {
              this.set('type', 'value');
              this.set('editActive', false);

              return run.next(() => {
                this.$().closest('.rsa-query-meta').find('.rsa-query-fragment.edit-active input').focus();
              });
            }
          }

          if (event.code === 'Space') {
            if (select.results.length === 1) {
              select.actions.select(select.results[0]);

              run.next(() => {
                if (type === 'meta') {
                  this.set('type', 'operator');
                } else if (type === 'operator') {
                  this.set('type', 'value');
                }
              });
            } else {
              if (type != 'value') {
                const match = select.results.find((result) => {
                  if (type === 'meta') {
                    return result.metaName === select.searchText.toLowerCase();
                  } else {
                    const valueToMatch = select.searchText.slice(filter.length, select.searchText.length);
                    return result === valueToMatch;
                  }
                });

                if (!isEmpty(match)) {
                  select.actions.select(match);
                }
              }
            }
          }

          if (event.code === 'ArrowLeft') {
            if (cursorPosition === 0 && isEmpty(input.val())) {
              if (filterIndex !== 0) {
                select.actions.close();

                if (!completed) {
                  resetFilter(this);
                }
              }
            }
          }

          if (event.code === 'Enter') {
            if (type === 'value') {
              if (completed || select.searchText.length > filter.length) {
                const currentFilter = meta.length + operator.length + 2;
                const valueToSet = select.searchText.slice(currentFilter, select.searchText.length);
                this.set('value', valueToSet);
                this.set('editActive', false);

                if (filterRecord === filterList.get('lastObject')) {
                  insertEmptyFilter(filterList, filterList.get('length'));
                }

                return run.schedule('afterRender', () => {
                  this.$().closest('.rsa-query-meta').find('.rsa-query-fragment.edit-active input').focus();
                });
              }
            }

            if (cursorPosition === 0 && isEmpty(filter) && (filterRecord === filterList.get('lastObject'))) {
              this.executeQuery(filterList);
            }
          }

          if (completed) {
            if (cursorPosition >= (metaLength + operatorLength + 2)) {
              this.set('type', 'value');
            } else if (cursorPosition >= (metaLength + 1)) {
              this.set('type', 'operator');
            } else {
              this.set('type', 'meta');
            }
          } else {
            if (withMeta && withOperator && !withValue) {
              if (cursorPosition >= (metaLength + operatorLength + 2)) {
                this.set('type', 'value');
              } else if (cursorPosition >= (metaLength + 1)) {
                this.set('type', 'operator');
              } else {
                this.set('type', 'meta');
              }
            } else if (withMeta && !withOperator && !withValue) {

              if (cursorPosition >= metaLength) {
                this.set('type', 'operator');
              } else {
                this.set('type', 'meta');
              }
            } else if (empty) {
              this.set('type', 'meta');
            }
          }
        }
      });
    },

    // Filter displayed options
    lookup(searchTerm) {
      searchTerm = searchTerm.toLowerCase();

      const {
        completed, options, type, onMeta, meta, operator, value, onOperator, withMeta, withOperator, withValue
      } = this.getProperties('completed', 'options', 'type', 'onOperator', 'meta', 'operator', 'value', 'onMeta', 'withMeta', 'withOperator', 'withValue');

      if (isEmpty(options) || isEmpty(searchTerm)) {
        return;
      }

      return options.filter((option) => {
        if (completed) {
          if (onMeta) {
            searchTerm = searchTerm.replace(` ${operator}`, '').replace(` ${value}`, '');
          } else if (onOperator) {
            searchTerm = searchTerm.replace(`${meta} `, '').replace(` ${value}`, '');
          }
        } else {
          if (withMeta && withOperator && !withValue) {
            searchTerm = searchTerm.replace(`${meta} `, '').replace(`${operator} `, '');
          } else if (withMeta && !withOperator && !withValue) {
            searchTerm = searchTerm.replace(`${meta} `, '');
          }
        }

        searchTerm = searchTerm.trim();
        if (type === 'meta') {
          return option.metaName.indexOf(searchTerm) !== -1 || option.displayName.indexOf(searchTerm) !== -1;
        } else {
          return option.indexOf(searchTerm) !== -1;
        }
      });
    },

    // User makes selection via typeahead
    updateFilter(selection, select) {
      if (isEmpty(selection)) {
        return select.actions.open();
      }

      const { type, completed } = this.getProperties('completed', 'type');

      if (type === 'meta') {
        this.set('meta', selection.metaName);
        this.set('metaFormat', selection.format);
        this.set('type', 'operator');
      } else if (type === 'operator') {
        this.set('operator', selection);
        this.set('type', 'value');
      }

      if (completed) {
        this.set('editActive', false);
        this.set('type', 'value');
        this.$().closest('.rsa-query-meta').find('.rsa-query-fragment.edit-active input').focus();
      }

      run.next(() => {
        select.actions.open();
      });
    },

    editFilter() {
      this.toggleProperty('editActive');

      run.next(() => {
        this.$('input').prop('type', 'text').prop('spellchek', false).focus();
      });
    },

    deleteFilter() {
      this.deleteFilter(this.get('filterRecord'));
      this.$().closest('.rsa-query-meta').find('.rsa-query-fragment.edit-active input').focus();
    }
  }

});
