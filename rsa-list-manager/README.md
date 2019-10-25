# RSA List Manager

The list manager can be used in your template as follows:
```
{{#list-manager
  stateLocation=stateLocation
  listName=name
  list=list
  modelName=modelName
  shouldSelectedItemPersist=false
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
    itemTransform=(action 'transformMyItem')
    isValidItem=(action 'validateMyItem')
    as |details|}}
    {{your-component
      myItem=details.item
      myItemEditFunction=details.itemEdited}}
  {{/manager.details}}

{{/list-manager}}
```

## Inputs
* `stateLocation` *String*, __required__, Location of list-manager within state e.g. 'listManagers.columnGroups', 'listManagers.profile'
* `listName`, *String*, __required__, Caption for List. Should be plural.
* `list`, *Object[]*, __required__, The list to be rendered. Object properties:
  * name, *String*, __required__
  * id, *String*, __required__
  * isEditable, *Boolean*, __optional__
  * isDeletable, *Boolean*, __optional__
  * undeletableReason, *String*, __optional__
* `modelName`, *String*, __required__, API socket model name and post request header name both being identical, e.g. 'columnGroup'
* `shouldSelectedItemPersist`, *Boolean*, __optional__, True if item can be "selected" and persist e.g. column groups, false if item is used once e.g. profiles
* `selectedItemId`, *String*, __optional__, The id of the option that needs to be displayed with listName for the caption and highlighted as selected in the list
* `helpId`, *Object*, __optional__, Object properties:
  * moduleId, *String*, __required__
  * topicId, *String*, __required__

## Actions
* `itemSelection`, *Action*, __required__,  An action to execute when an item is selected.
* `itemTransform`, *Action*, __optional__,  An action to execute when an item from server needs mapping to another format for UI. e.g. metaName: 'foo' > field: 'foo'
* `isValidItem`, *Action*, __optional__,  Any custom validation for editedItem.

## NOTE
* `details.item` is the originalItem object yielded to `your-component`
* `details.itemEdited` is a function that takes an editedItem from `your-component`
* list-manager validates if the editedItem has name and does not provide any other validation.
* For any other validation, a custom validation function should be passed to `isValidItem`.
* refer `investigate-events/events-table-container/header-container/column-groups` for usage
