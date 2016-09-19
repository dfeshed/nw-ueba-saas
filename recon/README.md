# Recon

The recon addon can be used by defining just one component in your template:

```hbs
{{recon-container
  endpointId=model.recon.endpointId
  eventId=model.recon.item.sessionId
  meta=model.recon.item.metas
  index=model.recon.index
  total=model.recon.total
  closeAction=(route-action 'reconClose')
  expandAction=(route-action 'reconExpand')
  shrinkAction=(route-action 'reconShrink')
  language=model.queryNode.value.language
  aliases=model.queryNode.value.aliases
}}
```

## Inputs

* `endpointId`, `Number`, __required__, the id for the endpoint for the event being reconstructed
* `eventId`, `Number`, __required__, the id of the event being reconstructed
* `meta`, `Array: Meta`, an array of meta for meta details, if this array is not provided, Recon will fetch it
* `index`, `Number`, 0 based index of item in result set (if viewing item from result set)
* `total`, `Number`, Total number of results in result (if viewing item from result set)
* `closeAction`, `Action`, an action to execute when recon wants to close itself
* `expandAction`, `Action`, an action to execute when recon wants to expand itself
* `shrinkAction`, `Action`, an action to execute when recon wants to shrink itself
* `language`
* `aliases`