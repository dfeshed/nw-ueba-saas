# Recon

The recon addon can be used by defining just one component in your template:

```hbs
{{recon-container
  aliases=(readonly aliases)
  endpointId=(readonly serviceId)
  eventId=(readonly sessionId)
  eventType=(readonly eventType)
  index=(readonly selectedIndex)
  isExpanded=(readonly isExpanded)
  language=(readonly language)
  meta=(readonly metas)
  queryInputs=(readonly queryInputs)
  size=(readonly reconSize)
  total=(readonly totalCount)
  closeAction=closeAction
  expandAction=expandAction
  linkToFileAction=reconLinkToFile
  shrinkAction=shrinkAction
}}
```

## Inputs

* `aliases`, *Array*, Alias data.
* `endpointId`, *Number*, __required__, The id for the service for the event being reconstructed.
* `eventId`, *Number*, __required__, The id of the session being reconstructed.
* `eventType`, *String*, The type of event being reconstructed. Value can be one of *`component-lib/constants/event-types.js`*.
* `index`, *Number*, Zero based index of item in result set (if viewing item from result set).
* `isExpanded`, *Boolean*, Whether or not the recon panel is currently 'expanded'.
* `language`, *Array*, Language data.
* `meta`, *Array*, An array of meta for meta details. If this array is not provided, Recon will fetch it.
* `queryInputs`, *Object*, Various params which are part of the query, including the data needed for executing actions in the context menu.
* `total`, *Number*, Total number of results in result (if viewing item from result set).

## Actions
* `closeAction`, *Action*, An action to execute when recon wants to close itself.
* `expandAction`, *Action*, An action to execute when recon wants to expand itself.
* `linkToFileAction`, *Action*, An action to execute when recon wants to invoke a link to another event query.
* `shrinkAction`, *Action*, An action to execute when recon wants to shrink itself.
