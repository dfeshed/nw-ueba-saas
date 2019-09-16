# RSA List Manager

The list manager can be used in your template as follows:
```
{{#list-manager
  listName=name
  list=list
  selectedItem=selectedItem
  itemSelection=handleSelection
  helpId=helpId
  as |manager|}}

  {{!-- optional -- renders the filter component--}}
  {{manager.filter filterAction=customFilterAction}}

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
* `listName`, *String ending with s(plural)*, __required__, Caption for List.
* `list`, *Array of objects with name(required), id(required), isEditable(boolean, optional) parameters*, __required__, The list to be rendered.
* `selectedItem`, *Object*,__optional__, The option that needs to be displayed with listName for the caption and highlighed as selected in the list
* `helpId`, *Object*,__optional__, Object with moduleId & topicId

## Actions
* `itemSelection`, *Action*, __required__,  An action to execute when an item is selected.
* `filterAction`, *Action*, __optional__,
  * Custom action to filter the list when input is given to the filter component.
  * If provided, the custom action must return the filtered list.
  * If not provided, default filtering of the manager will be used.
