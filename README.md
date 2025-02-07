# Titlu proiect
Diet Diary

## Descriere
Jurnalul alimentar care va ajuta persoanele care vor să își documenteze mai bine obiceiurile alimentare, dar și aspectul fizic. Acesta este conceput să ofere o metodă simplă și organizată prin care utilizatorul își poate urmări aportul caloric, modificările în greutate, dar și modificările în dimensiunile corpului. Acesta este dedicat pentru documentarea progresului și a modificărilor care apar de-a lungul tmpului.

## Obiective
Persoana care utilizează jurnalul va putea să:

* vadă informațiile despre greutate sub formă de grafic
* gestioneze informații legate de programări la nutriționist
      - adăugare de programări noi
      - vizualizarea programărilor existente sub formă de tabel
      - modifice programări existente
* introducă informații legate de greutate
    - numărul de kilograme
    - data la care au fost introduse
* introducă informații legate de alimentele consumate
    - numărul de calorii
    - ora din zi
    - data consumului
* introducă informații despre dimensiunile corpului (talie, brațe, picioare)

## Arhitectura
Aplicația este împărțită în 4 pachete și 14 clase:
* Autentificare:
      - Login
      - Main
      - Register
* BazaDate:
      - ConexiuneBazaDate
* DietDiary:
      - Diagrama
          - DataPoint
      -MainMenu
      - MonitorizareAlimentatie
      - MonitorizareDimensiuni
      - MonitorizareGreutate
      - ProgramariNutritionist
* TesteUnitare:
      - ConexiuneBazaDateTest
      - LoginTest
      - RegisterTest
