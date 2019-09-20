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
  {{#manager.details as |item|}}
    {{!-- render item details as desired --}}
  {{/manager.details}}

{{/list-manager}}
```

## Inputs
* `stateLocation`, *String*, __required__, Location of list-manager within state e.g. 'listManagers.columnGroups'
* `listName`, *String ending with s(plural)*, __required__, Caption for List
* `list`, *Array of objects with name(required), id(required), isEditable(boolean, optional) parameters*, __required__, The list to be rendered
* `selectedItemId`, *String*,__optional__, The id of the option that needs to be displayed with listName for the caption and highlighted as selected in the list
* `helpId`, *Object*,__optional__, Object with moduleId & topicId

## Actions
* `itemSelection`, *Action*, __required__,  An action to execute when an item is selected.
