"Return a list of all the combinations of a list of tokens taken k items at a time"

| combinations choices |
combinations := [:tokens :k |
(k <= 0) ifTrue: [OrderedCollection new] ifFalse: [
((tokens size == 0) or: [k == tokens size])
	ifTrue: [OrderedCollection with: tokens]
	ifFalse: [
		choices := combinations value: (tokens copyFrom: 2 to: tokens size) value: k-1.
		choices := (choices isEmpty
			ifTrue: [choices add: (OrderedCollection with: tokens first); yourself]
			ifFalse: [choices collect: [:choice |
				(OrderedCollection with: tokens first)
					addAll: choice;
					yourself]])
				addAll: (combinations value: (tokens copyFrom: 2 to: tokens size) value: k);
				yourself]]].

^combinations value: #(a b c d e f) asOrderedCollection value: 3
