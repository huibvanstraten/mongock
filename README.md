🔄 Database migrations

Migrations are implemented as change units (@ChangeUnit) in Mongock.

001 — Create collection

Creates the users collection if missing.

Rollback: drops the collection.

002 — Add initial validator + indexes

Applies a permissive $jsonSchema validator requiring username, email, externalId.

Allows newsletterOptIn as optional (bool or null).

Adds:

Unique index on externalId.

Non-unique index on email.

Rollback: removes validator (sets empty validator).

003 — Backfill newsletter field

Sets newsletterOptIn=false for documents missing the field.

Rollback: unsets newsletterOptIn where false.

004 — Strict validator

Tightens the $jsonSchema to require newsletterOptIn (bool).

Sets validationLevel=strict and validationAction=error.

Rollback: reverts back to the permissive schema of 002.

Each migration is idempotent: safe to re-run and predictable across environments.