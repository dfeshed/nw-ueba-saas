# Recon

The recon addon can be used by defining just one component in your template:

```hbs
{{recon-container
  endpointId=state.recon.serviceId
  eventId=state.recon.sessionId
  meta=state.recon.metas
  contextMenuItems=state.recon.contextMenuItems
  queryInputs=state.recon.queryInputs
  index=state.recon.index
  total=state.recon.total
  isExpanded=state.recon.isExpanded
  closeAction=closeAction
  expandAction=(route-action 'reconExpand')
  shrinkAction=(route-action 'reconShrink')
  language=state.dictionaries.language
  aliases=state.dictionaries.aliases
}}
```

## Inputs

* `endpointId`, `Number`, __required__, the id for the service for the event being reconstructed
* `eventId`, `Number`, __required__, the id of the session being reconstructed
* `meta`, `Array: Meta`, an array of meta for meta details, if this array is not provided, Recon will fetch it
* `contextMenuItems`, `Array: Object`, List of items to be shown in the context menu
* `queryInputs`, `Object`, Various params which are part of the query, including the data needed for executing actions in the context menu
* `index`, `Number`, 0 based index of item in result set (if viewing item from result set)
* `total`, `Number`, Total number of results in result (if viewing item from result set)
* `isExpanded`, `boolean`, whether or not the recon panel is currently 'expanded'
* `closeAction`, `Action`, an action to execute when recon wants to close itself
* `expandAction`, `Action`, an action to execute when recon wants to expand itself
* `shrinkAction`, `Action`, an action to execute when recon wants to shrink itself
* `language`
* `aliases`