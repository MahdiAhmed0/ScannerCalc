# ScannerCalc – DFA-Based Lexical Scanner in Java

ScannerCalc is a hand-written lexical analyzer implemented in Java using a **Deterministic Finite Automaton (DFA)**.  
It tokenizes a simple calculator-style language with identifiers, numbers, operators, comments, and keywords.

This project demonstrates how classical compiler scanner design (state tables + character classification) can be implemented without generator tools like Lex/Flex.

---

## Features

- Table-driven **DFA scanner**
- Explicit **character classification**
- Support for:
  - Identifiers and keywords
  - Integer and decimal numbers
  - Arithmetic operators: `+ - * /`
  - Assignment operator: `:=`
  - Parentheses
  - Single-line and block-style comments
  - Whitespace handling
- Keyword recognition (`read`, `write`)
- Lookahead and backtracking using `readChar()` / `unread()`

---

## Token Types

| Token | Description |
|------|------------|
| `T_NUMBER` | Integer or floating-point numbers |
| `T_IDENT` | Identifiers |
| `T_PLUS` | `+` |
| `T_MINUS` | `-` |
| `T_TIMES` | `*` |
| `T_DIV` | `/` |
| `T_ASSIGN` | `:=` |
| `T_LPAREN` | `(` |
| `T_RPAREN` | `)` |
| `T_COMMENT` | Comment |
| `T_WHITESPACE` | Space / newline (ignored) |
| `read`, `write` | Recognized as keywords |

---

## DFA Design

The scanner is implemented using:
- **Character classes** (SPACE, DIGIT, LETTER, etc.)
- **State transition table (`nextState`)**
- **Token table (`tokenTab`)** mapping final states to token types
- Longest-match strategy using remembered states
---
