# OSCAL-Sherpa
An open source assistant to handle any OSCAL described certification.

Looking to develop a server performing the following:
- import data from OSCAL
- compare with an existing file of another version and import existing certified items from the older version.
- for each item, define work items, such as:
    1. Automated tests
    2. Tickets - when the ticket is solved, the item is considered passed
    3. Employment manual entry for users members of specific groups
- interact with LDAP, ticket system, document agreement systems.
- computer score per item
- handle expiration of items by triggering alarms, automated tests, write tickets.
- show a dashboard with the current state of certifications.

The system is OSCAL centric, the OSCAL file shall be stored in the database as a bulk, but other information artifacts must be stored in a structured fashion.

The system connectes to:
- LDAP - collect employees from specific groups, performs login
- Mail system - send alarms
- Ticket system - create tickets and monitors their fulfillment
- Document signing system - employees have to acknowledge reading parts of the employment manual.
  
