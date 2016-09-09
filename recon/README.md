# Recon

The recon addon can be used by defining just one component in your template:

```hbs
{{recon-container
  endpointId=model.recon.endpointId
  eventId=model.recon.item.sessionId
  meta=model.recon.item.metas
  title=model.recon.title
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
* `title`, `String`, title to display, i.e. `Event Reconstruction (3 of 2567)`
  * Defaults to `Event Reconstruction`
* `closeAction`, `Action`, an action to execute when recon wants to close itself
* `expandAction`, `Action`, an action to execute when recon wants to expand itself
* `shrinkAction`, `Action`, an action to execute when recon wants to shrink itself
* `language`
* `aliases`