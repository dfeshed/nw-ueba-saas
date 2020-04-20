rsa-data-filters
==============================================================================

``rsa-data-filters`` component will render the configured filter controls and emits a query object based on a user’s selected filter values.

Usage
------------------------------------------------------------------------------

To use `rsa-data-filters` one need to pass the filter configuration to the component. Based on the passed down configuration filter controls are rendered.

```
{{rsa-data-filters config=config onFilterChange=(action 'reloadData')}}
```
Ex Config: 
```
const config = [
  {
    name: 'fileName',
    type: 'text'
    label: 'File Name'
  },
  {
    name: 'fileStatus',
    type: 'list'
    label: 'File Status'
    options: [
      { value: 'blacklisted', label: 'Black Listed'}
      { value: 'whitelisted', label: 'White Listed'}
    ]
  }
]
actions: { 
  reloadData(allSelectedFilters) {
    // where allSelectedFilters are user applied filter values
  }
}
```

Configuration
------------------------------------------------------------------------------

### Component Configuration
| Name | Type | Description |
| ---- | ---- | ---------- |
| config | `Array` | Required. Array of filter configuration objects, each object contains the information about type of the filter and filter options.Refer Filter Config Options for more details |
| onFilterChange | `Function` | Required. The function to be invoked when any of the filter query changes, when function is get called it sends all the selected filters |
| clearFormOnReset | `Boolean` | Optional. Defaults false and on click of reset button filter will go back to whatever the component was provided via the config. When value is set to true it clears the form values. Both the case onFilterChange will be called with new values   |
| showSaveFilterButton | `Boolean` | Optional. Button to save applied filter and on click button will call the save filter function with list of queries. Defaults to false  |
| onSave | `Function` | Optional. The function to be invoked when the user clicks on the save button |

#### onFilterChange(selectedFilters)
Function get called whenever user modifies the filter. User selected filters are passed as array of filter objects.

`selectedFilters` has the following format

| Filter Type | Output |
| ---- | ---------- |
| text | `{ name: 'fileName', value: 'malaware.exe' operator: 'IN' }` Where operator will be `IN` or `LIKE` |
| list | `{ name: 'status', value: ['black list', 'white list' ], operator: 'IN' }` Where operator will be `IN` always |
| dropdown | `{ name: 'status', value: ['black list', 'white list' ], operator: 'IN' }` Where operator will be `IN` always |
| range | `{ name: 'status', value: [10, 20], operator: 'BETWEEN' }` Where operator will be `BETWEEN` always |
| number | `{ name: 'status', value: [10, 20], operator: 'BETWEEN' unit: 'MB' }` When operator is `BETWEEN` then pass two values in array else one for others(`LESS_THAN`, `GREATER_THAN` and `EQUALS`) |


### Filter Configuration

##### Filter Type
| Filter | Type | UI |
| ---- | ---- | -------- |
| Text | `text` | Input field with options to select the supported operators |
| List | `list` | List of checkboxes |
| Range | `range` | Slider to select the min and max value |
| Number | `number` | Text box with unit and operator selectors |
| Dropdown | `dropdown` | Dropdown select list of values |

##### Common Options
| Name | Type | Description |
| ---- | ---- | ---------- |
| name | `String` | Database column name, it's mandatory |
| type | `String` | Type of the filter. ony one of `text`, `list`, `number`, `range`, `dropdown` can be used |
| label | `String` | Text to be displayed above the filter control |

##### Option type `text`
| Name | Type | Description |
| ---- | ---- | ---------- |
| filterValue | `Object` | If provided filter will be pre-populated with value.  Ex: `{ value: 10, operator: IN }` |

##### Option type `list`
| Name | Type | Description |
| ---- | ---- | ---------- |
| listOptions | `Array` | Required. Array of objects with name and label Ex: `[{ name: 'blacklisted', label: 'Black Listed' }]` Where name should match the database table value. List Options are rendered as checkboxes   |
| filterValue | `Array` | If provided filter will be pre-populated with value. Ex: `[blacklisted, graylisted]`|

##### Option type `range`
| Name | Type | Description |
| ---- | ---- |  ---------- |
| min | `Number` | Required. Minimum value for slider   |
| max | `Number` | Required. Maximum value for slider   |
| filterValue | `Array` | If provided filter will be pre-populated with value. Ex: `[20, 10]`  |

##### Option type `number`
| Name | Type | Description |
| ---- | ---- |  ---------- |
| units | `Array` | Optional. Units for denoting the number ex: for memory it should MB,KB,GB etc. unit object: `{ unit: 'kb', label: 'KB' }` |
| filterValue | `Object` | If provided filter will be pre-populated with value and unit. Ex: `{ value: 10, unit: MB, operator: LESS_THAN }` |

##### Option type `dropdown`
| Name | Type | Description |
| ---- | ---- | ---------- |
| selectOptions | `Array` | Required. Array of objects with name and label Ex: `[{ name: 'blacklisted', label: 'Black Listed' }]` Where name should match the database table value.  |
| filterValue | `Array` | If provided filter will be pre-populated with value  Ex: `[blacklisted, graylisted]` |
