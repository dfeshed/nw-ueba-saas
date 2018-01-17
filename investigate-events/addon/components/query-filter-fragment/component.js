import EmberObject from 'ember-object';
import Component from 'ember-component';
import layout from './template';
import run from 'ember-runloop';
import { isEmpty } from 'ember-utils';
import computed, { equal, notEmpty, and, not } from 'ember-computed-decorators';
import contextMenuMixin from 'ember-context-menu';
import service from 'ember-service/inject';
import { validateIndividualQuery } from 'investigate-events/actions/query-validation-creators';
import { connect } from 'ember-redux';

const isFloat = (value) => {
  return value.includes('.') && (value - value === 0);
};

const isIPv4 = (value) => {
  return /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(value);
};

const isIPv6 = (value) => {
  return /^((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4}))*::((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4}))*|((?:[0-9A-Fa-f]{1,4}))((?::[0-9A-Fa-f]{1,4})){7}$/g.test(value);
};

const isInt = (value) => {
  return !value.includes('.') && (value - value === 0);
};

const dispatchToActions = {
  validateIndividualQuery
};

const QueryFragmentComponent = Component.extend(contextMenuMixin, {
  i18n: service(),

  layout,

  classNames: ['rsa-query-fragment'],

  classNameBindings: [
    'editActive', 'selected', 'empty',
    'typing', 'prevIsEditing', 'isExpensive',
    'queryFragmentInvalid', 'metaIndex'
  ],

  type: 'meta',

  meta: null,

  operator: null,

  value: null,

  filterIndex: 0,

  editActive: false,

  selected: false,

  metaFormat: null,

  isExpensive: false,

  apiMetaMessage: null,

  wasValidated: false,

  saved: false,

  validateWithServer: true,

  @equal('type', 'meta') onMeta: false,
  @equal('type', 'operator') onOperator: false,
  @equal('type', 'value') onValue: false,

  @notEmpty('meta') withMeta: false,
  @notEmpty('operator') withOperator: false,
  @notEmpty('value') withValue: false,

  @and('withMeta', 'withOperator', 'withValue') completed: false,
  @not('withMeta', 'withOperator', 'withValue') empty: false,

  @computed('metaFormat', 'metaIndex')
  operatorOptions(metaFormat, metaIndex) {
    const efficientIndex = metaIndex === 'value';
    const eq = { displayName: '=', isExpensive: !efficientIndex };
    const notEq = { displayName: '!=', isExpensive: !efficientIndex };
    const lt = { displayName: '<', isExpensive: !efficientIndex };
    const lte = { displayName: '<=', isExpensive: !efficientIndex };
    const gt = { displayName: '>', isExpensive: !efficientIndex };
    const gte = { displayName: '>=', isExpensive: !efficientIndex };
    const exists = { displayName: 'exists', isExpensive: false };
    const notExists = { displayName: '!exists', isExpensive: false };
    const contains = { displayName: 'contains', isExpensive: !efficientIndex };
    const begins = { displayName: 'begins', isExpensive: !efficientIndex };
    const ends = { displayName: 'ends', isExpensive: !efficientIndex };

    if (metaFormat === 'Text') {
      return [ eq, notEq, exists, notExists, contains, begins, ends ];
    } else if (metaFormat === 'IPv4' || metaFormat === 'IPv6') {
      return [ eq, notEq, exists, notExists ];
    } else {
      return [ eq, notEq, lt, lte, gt, gte, exists, notExists ];
    }
  },

  @computed('selected', 'filterList', 'deleteFilter', 'i18n', 'executeQuery')
  contextItems(selected, filterList, deleteFilter, i18n, executeQuery) {
    if (selected) {
      return [{
        label: i18n.t('queryBuilder.querySelected'),
        action() {
          executeQuery(filterList.filterBy('selected', true));
        }
      }, {
        label: i18n.t('queryBuilder.querySelectedNewTab'),
        action() {
          executeQuery(filterList.filterBy('selected', true), true);
        }
      }, {
        label: i18n.t('queryBuilder.delete'),
        action() {
          deleteFilter(filterList.filterBy('selected', true));
          executeQuery(filterList);
        }
      }];
    }
  },

  @computed('meta', 'operator', 'value')
  filter(meta, operator, value) {
    return `${meta || ''} ${operator || ''} ${value || ''}`.trim();
  },

  @computed('metaOptions', 'metaOptions.length')
  filteredMetaOptions(metaOptions) {
    if (!isEmpty(metaOptions)) {
      if (metaOptions.asMutable) {
        metaOptions = metaOptions.asMutable();
      }

      return metaOptions.filter((option) => {
        const keyIndexes = ['none', 'key', 'value'];
        const keyIndexType = option.flags & '0xF';

        const withIndex = keyIndexes[keyIndexType - 1] != 'none';
        const isTime = option.metaName === 'time';
        const isSessionId = option.metaName === 'sessionid';

        return withIndex || isTime || isSessionId;
      });
    }
  },

  @computed('filteredMetaOptions', 'filteredMetaOptions.length')
  sortedMetaOptions(metaOptions) {
    if (!isEmpty(metaOptions)) {
      if (metaOptions.asMutable) {
        metaOptions = metaOptions.asMutable();
      }

      return metaOptions.sort((a, b) => {
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

  @computed('type', 'sortedMetaOptions', 'operatorOptions', 'sortedMetaOptions.length', 'valueOptions', 'valueOptions.length', 'operatorOptions.length')
  options(type, sortedMetaOptions, operatorOptions) {
    if (type == 'value') {
      return [];
    } else if (type === 'meta') {
      return sortedMetaOptions;
    } else {
      return operatorOptions;
    }
  },

  @computed('filterList', 'filterIndex', 'filterList.@each.editActive')
  prevIsEditing(filterList, filterIndex) {
    if (!isEmpty(filterList)) {
      const prev = filterList.objectAt(filterIndex - 1);
      return isEmpty(prev) ? false : prev.get('editActive');
    }
  },

  doubleClick() {
    if (this.get('saved') && !this.get('editActive')) {
      this.send('editFilter');
    }
  },

  didInsertElement() {
    this._super(...arguments);
    this.$('input').prop('type', 'text').prop('spellcheck', false);
  },

  didReceiveAttrs() {
    this._super(...arguments);

    const { meta, operator, value } = this;
    // checking for null as this hook is called with null values multiple times.
    if (meta != null && operator != null && value != null) {
      // api needs a string instead of object
      const filterText = `${meta} ${operator} ${value}`;
      if (this.get('validateWithServer') && (!this.get('wasValidated') || filterText != this.get('filter'))) {
        this._prevalidation();
        if (!this.get('queryFragmentInvalid')) {
          this.send('validateIndividualQuery', filterText, this._validateComplete.bind(this));
        }
      }
    }
  },

  _validateComplete(isValid, apiMetaMessage) {
    if (!isValid) {
      this.set('queryFragmentInvalid', true);
      this.set('wasValidated', true);

      if (apiMetaMessage.message) {
        this.set('apiMetaMessage', apiMetaMessage.message);
      }
    }
  },

  _prevalidation() {
    let isValid = true;
    let message = '';
    const i18n = this.get('i18n');
    const value = this.get('value');
    const format = this.get('metaFormat');

    if (isEmpty(value)) {
      return;
    }

    switch (format) {
      case 'TimeT':
        isValid = new Date(value) != 'Invalid Date';
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.time');
        }
        break;
      case 'Text':
        isValid = value.slice(0) != '"' || value.slice(-1) != '"';
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.text');
        }
        break;
      case 'IPv4':
        isValid = isIPv4(value);
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.ipv4');
        }
        break;
      case 'IPv6':
        isValid = isIPv6(value);
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.ipv6');
        }
        break;
      case 'UInt8':
        isValid = isInt(value);
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.uint8');
        }
        break;
      case 'UInt16':
        isValid = isInt(value);
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.uint16');
        }
        break;
      case 'UInt32':
        isValid = isInt(value);
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.uint32');
        }
        break;
      case 'Float32':
        isValid = isFloat(value);
        if (!isValid) {
          message = i18n.t('queryBuilder.validationMessages.float32');
        }
        break;
    }
    this._validateComplete(isValid, { message });
  },

  _insertEmptyFilter(index) {
    const list = this.get('filterList');
    const filter = EmberObject.create({
      meta: null,
      operator: null,
      value: null,
      filter: null,
      filterIndex: index || list.get('length'),
      editActive: true,
      selected: false
    });

    !isEmpty(index) ? list.addObject(filter) : list.insertAt(index, filter);
  },

  _resetFilter() {
    this.setProperties({
      meta: null,
      operator: null,
      value: null,
      selected: null,
      saved: false
    });
  },

  actions: {
    toggleSelect() {
      if (!this.get('editActive')) {
        this.toggleProperty('selected');
      }
    },

    onblur() {
      this.setKeyboardPriority(0);

      if (!this.get('saved')) {
        const list = this.get('filterList');
        const filterRecord = this.get('filterRecord');
        const lastObject = list.get('lastObject');
        const prunedList = list
          .without(lastObject)
          .without(filterRecord);

        prunedList.forEach((filter) => {
          const isActive = filter.get('editActive');

          if (isActive && isEmpty(filter.get('meta'))) {
            this.deleteFilter(filter);
          } else if (isActive) {
            filter.set('editActive', false);
          }
        });

        if (filterRecord === lastObject && isEmpty(this.$('input').val())) {
          this.deleteFilter(filterRecord);
          this._insertEmptyFilter(-1);
        }
      }
    },

    onfocus(select) {
      this.setKeyboardPriority(1);

      const list = this.get('filterList');
      const prunedList = list
        .without(list.get('lastObject'))
        .without(this.get('filterRecord'));

      prunedList.forEach((filter) => {
        if (filter.get('editActive') && isEmpty(filter.get('meta'))) {
          this.deleteFilter(filter);
        } else {
          filter.set('selected', false);
          filter.set('editActive', false);
        }
      });

      this.$('input').prop('type', 'text').prop('spellcheck', false);
      select.actions.open();
    },

    // User takes action via keyboard
    parseInput(select, event) {
      const input = this.$('input');
      const inputVal = this.$('input').val();
      const cursorPosition = input.get(0).selectionStart;
      const {
        filter, filterRecord, filterList, type, filterIndex,
        meta, operator, saved
      } = this.getProperties(
        'filter', 'filterRecord', 'type', 'filterList', 'filterIndex',
        'meta', 'operator', 'saved'
      );

      if (isEmpty(input.get(0))) {
        return;
      }

      if (isEmpty(inputVal)) {
        this.setProperties({
          type: 'meta',
          meta: null,
          operator: null,
          value: null,
          typing: false
        });

        if (this.get('filterRecord') != this.get('filterList').get('lastObject')) {
          input.width(8);
        }
      } else {
        this.set('typing', true);

        if (this.get('filterRecord') != this.get('filterList').get('lastObject')) {
          input.width((inputVal.length + 1) * 8);
        }
      }

      const pressedSpace = event.keyCode === 32 || event.which === 32 || event.code === 'Space';
      const pressedEnter = event.keyCode === 13 || event.which === 13 || event.code === 'Enter';
      const pressedEscape = event.keyCode === 27 || event.which === 27 || event.code === 'Escape';
      const pressedLeft = event.keyCode === 37 || event.which === 37 || event.code === 'ArrowLeft';
      const pressedRight = event.keyCode === 39 || event.which === 39 || event.code === 'ArrowRight';
      const pressedBackspace = event.keyCode === 8 || event.which === 8 || event.code === 'Backspace';
      const pressedDelete = event.keyCode === 46 || event.which === 46 || event.code === 'Delete';

      if (pressedLeft) {
        if (cursorPosition === 0 && isEmpty(inputVal)) {
          if (filterIndex !== 0) {
            select.actions.close();

            if (!saved) {
              this._resetFilter();
            }
          }
        }
      }

      if (pressedEscape) {
        select.actions.close();
        input.blur();

        if (!saved) {
          this._resetFilter();
          this.set('type', 'meta');

          if (filterRecord != filterList.get('lastObject')) {
            filterList.removeObject(filterRecord);
          }
        } else {
          this.set('type', 'value');
          this.set('editActive', false);
        }
      }

      if (pressedSpace) {
        if (type != 'value') {
          if (select.results.length === 1) {
            select.actions.select(select.results[0]);

            if (type === 'meta') {
              this.set('type', 'operator');
            } else if (type === 'operator') {
              this.set('type', 'value');
            }
          } else {
            const match = select.results.find((result) => {
              if (type === 'meta') {
                return result.metaName === select.searchText.toLowerCase();
              } else {
                const valueToMatch = select.searchText.slice(filter.length, select.searchText.length);
                return result.displayName === valueToMatch;
              }
            });

            if (!isEmpty(match)) {
              select.actions.select(match);
            }
          }
        }
      }

      if (pressedEnter) {
        if (isEmpty(select.highlighted) && isEmpty(inputVal) && (filterRecord === filterList.get('lastObject'))) {
          return this.executeQuery([]);
        }

        if (!isEmpty(inputVal)) {
          if (this.get('saved') && (filter === inputVal)) {
            this.set('editActive', false);
          } else {
            let updatedValue;

            const updatedMeta = this.get('metaOptions').find((option) => {
              return inputVal.includes(option.metaName) && inputVal.charAt(option.metaName.length) != '.';
            });

            if (updatedMeta) {
              const keyIndexes = ['none', 'key', 'value'];
              const keyIndexType = updatedMeta.flags & '0xF';

              this.setProperties({
                meta: updatedMeta.metaName,
                metaFormat: updatedMeta.format,
                metaIndex: keyIndexes[keyIndexType - 1]
              });
            }

            const updatedOperator = this.get('operatorOptions').find((option) => {
              if (inputVal.includes('!exists')) {
                return option.displayName === '!exists';
              } else if (inputVal.includes('<=')) {
                return option.displayName === '<=';
              } else if (inputVal.includes('>=')) {
                return option.displayName === '>=';
              } else if (inputVal.includes('!=')) {
                return option.displayName === '!=';
              } else {
                return inputVal.includes(option.displayName);
              }
            });

            if (updatedOperator) {
              const operatorIndex = inputVal.indexOf(updatedOperator.displayName);
              const textLength = inputVal.length;
              updatedValue = inputVal.slice(operatorIndex + updatedOperator.displayName.length, textLength).trim();
              this.set('operator', updatedOperator.displayName);
              this.set('isExpensive', updatedOperator.isExpensive);

              if (['exists', '!exists'].includes(updatedOperator.displayName)) {
                this.set('saved', true);
              }
            }

            const isExistsOperator = inputVal.includes('exists');
            if (!isEmpty(updatedMeta) && !isEmpty(updatedOperator) && (!isEmpty(updatedValue) || isExistsOperator)) {
              if (this.get('metaFormat') === 'Text' && !isExistsOperator) {
                updatedValue = `"${updatedValue.replace(/['"]/g, '')}"`;
              }

              // set query validation properties to default if editing
              // this is required apart from the other api call made in didInsertAttrs
              // as user can edit a invalid pill
              this.setProperties({
                value: updatedValue,
                editActive: false,
                wasValidated: true,
                queryFragmentInvalid: false,
                apiMetaMessage: null,
                saved: true
              });

              this._prevalidation();
              if (this.get('validateWithServer') && !this.get('queryFragmentInvalid')) {
                this.send('validateIndividualQuery', this.get('filter'), this._validateComplete.bind(this));
              }

              if (filterRecord === filterList.get('lastObject')) {
                this._insertEmptyFilter(filterList.indexOf(filterRecord) + 1);
              }

              run.next(() => {
                this.$().next('.rsa-query-fragment').find('input').focus();
              });
            }
          }
        }
      }

      if (pressedLeft || pressedRight || pressedDelete || pressedBackspace) {
        run.next(() => {
          let metaLength = 0;
          let operatorLength = 0;
          if (!isEmpty(meta)) {
            metaLength = meta.length;
          }

          if (!isEmpty(operator)) {
            operatorLength = operator.length;
          }

          const updatedCursorPosition = input.get(0).selectionStart;
          const metaAndOperatorLength = metaLength + operatorLength;

          if (this.get('empty')) {
            return;
          }

          if (updatedCursorPosition <= metaLength) {
            this.set('type', 'meta');
          } else if (updatedCursorPosition > metaLength && updatedCursorPosition <= metaAndOperatorLength + 1) {
            this.set('type', 'operator');
          } else if (updatedCursorPosition > metaAndOperatorLength) {
            this.set('type', 'value');
          }
        });
      }
    },

    // Filter displayed options
    lookup(searchTerm) {
      const {
        options, type, onMeta, meta, operator, completed,
        value, onOperator, withMeta, withOperator, withValue
      } = this.getProperties(
        'options', 'type', 'onOperator', 'meta', 'operator', 'completed',
        'value', 'onMeta', 'withMeta', 'withOperator', 'withValue'
      );

      if (isEmpty(options)) {
        return;
      }

      if (isEmpty(searchTerm)) {
        return;
      }

      searchTerm = searchTerm.toLowerCase();

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

        if (type === 'meta') {
          return option.metaName.indexOf(searchTerm) !== -1 || option.displayName.indexOf(searchTerm) !== -1;
        } else {
          return isEmpty(searchTerm) || option.displayName.slice(0, searchTerm.length) === searchTerm;
        }
      });
    },

    // User makes selection via typeahead
    updateFilter(selection, select) {
      const { filterList, type, filterRecord } = this.getProperties('filterList', 'type', 'filterRecord');
      const input = this.$('input');
      const inputVal = input.val();

      if (isEmpty(selection) && select.results.length === 1) {
        selection = select.results[0];
      } else if (isEmpty(selection)) {
        return;
      }

      if (type === 'meta') {
        if (inputVal.indexOf(this.get('value')) === -1) {
          this.set('value', null);
        }

        if (inputVal.indexOf(this.get('operator')) === -1) {
          this.set('operator', null);
        }

        const keyIndexes = ['none', 'key', 'value'];
        const keyIndexType = selection.flags & '0xF';

        if (this.get('filterRecord') != this.get('filterList').get('lastObject')) {
          this.$('input').width(selection.metaName.length * 8);
        }

        this.setProperties({
          meta: selection.metaName,
          metaFormat: selection.format,
          metaIndex: keyIndexes[keyIndexType - 1],
          type: 'operator'
        });
      } else if (type === 'operator') {
        if (inputVal && inputVal.indexOf(this.get('value')) === -1) {
          this.set('value', null);
        }

        this.setProperties({
          isExpensive: selection.isExpensive,
          operator: selection.displayName
        });

        if (['exists', '!exists'].includes(selection.displayName)) {
          this.setProperties({
            value: null,
            saved: true,
            editActive: false
          });

          this._insertEmptyFilter(filterList.indexOf(filterRecord) + 1);

          run.next(() => {
            this.$().next('.rsa-query-fragment').find('input').focus();
          });
        } else {
          this.set('type', 'value');
        }
      }
    },

    editFilter() {
      this.toggleProperty('editActive');
      this.set('type', 'value');

      run.next(() => {
        this.$('input').prop('type', 'text').prop('spellcheck', false).focus();

        if (this.get('filterRecord') != this.get('filterList').get('lastObject')) {
          this.$('input').width(this.$('input').val().length * 8);
        }
      });
    },

    deleteFilter() {
      this.deleteFilter(this.get('filterRecord'));
    }
  }

});

export default connect(null, dispatchToActions)(QueryFragmentComponent);
