# RSA List Manager

The list manager can be used in your template as follows:
```
{{#list-manager
  stateLocation=stateLocation
  listName=name
  list=list
  selectedItemId=selectedItemId
  itemSelection=handleSelection
  helpId=helpId
  as |manager|}}

  {{!-- optional -- renders the filter component--}}
  {{manager.filter}}

  {{!-- required -- renders the main list--}}
  {{#manager.itemList as |list|}}
    {{#list.item as |item|}}
      {{!--render the item in desired format --}}
    {{/list.item}}
  {{/manager.itemList}}

  {{!-- required --}}
  {{#manager.details
    itemTransform=someTransformFunction
    as |details|}}
    {{your-component
      myItem=details.item
      myItemEditFunction=details.itemEdited}}
  {{/manager.details}}

{{/list-manager}}
```

## Inputs
* `stateLocation`, *String*, __required__, Location of list-manager within state e.g. 'listManagers.columnGroups'
* `listName`, *String ending with s(plural)*, __required__, Caption for List
* `modelName`, *String*, __required__, for API socket model name and post request header name both being identical, e.g. 'columnGroup'
* `list`, *Array of objects with name(required), id(required), isEditable(boolean, optional) parameters*, __required__, The list to be rendered
* `selectedItemId`, *String*,__optional__, The id of the option that needs to be displayed with listName for the caption and highlighted as selected in the list
* `helpId`, *Object*,__optional__, Object with moduleId & topicId

## Actions
* `itemSelection`, *Action*, __required__,  An action to execute when an item is selected.
* `itemTransform`, *Action*, __optional__,  An action to execute when an item from server needs mapping to another format for UI. e.g. metaName: 'foo' > field: 'foo'

## NOTE
* `itemEdited` is a function that takes an editedItem from `your-component`
* list-manager validates if the editedItem has a unique name and does not provide any other validation.
* For any other validation, the editedItem passed to `itemEdited` should either be null or it must pass custom validity.
* refer `investigate-events/events-table-container/header-container/column-groups` for usage
