# Тесты к курсу «Парадигмы программирования»

[Условия домашних заданий](http://www.kgeorgiy.info/courses/paradigms/homeworks.html)

## Домашнее задание 2. Markdown to HTML
 * *Базовая*
    * [Исходный код тестов](java/md2html/Md2HtmlTest.java)
    * [Откомпилированные тесты](artifacts/md2html/Md2HtmlTest.jar)
 * *Link*
    * Добавьте поддержку ```[ссылок с _выделением_](https://kgeorgiy.info)```:
        ```&lt;a href='https://kgeorgiy.info'>ссылок с &lt;em>выделением&lt;/em>&lt;/a>```
    * [Исходный код тестов](java/md2html/Md2HtmlLinkTest.java)
    * [Откомпилированные тесты](artifacts/md2html/Md2HtmlLinkTest.jar)
 * *Underline*
    * Добавьте поддержку `++подчеркивания++`: `<u>подчеркивания</u>`
    * [Исходный код тестов](java/md2html/Md2HtmlUnderlineTest.java)
    * [Откомпилированные тесты](artifacts/md2html/Md2HtmlUnderlineTest.jar)


## Домашнее задание 1. Обработка ошибок

Модификации
 * *Базовая*
    * Класс `ExpressionParser` должен реализовывать интерфейс
        [Parser](java/expression/exceptions/Parser.java)
    * Классы `CheckedAdd`, `CheckedSubtract`, `CheckedMultiply`,
        `CheckedDivide` и `CheckedNegate` должны реализовывать интерфейс
        [TripleExpression](java/expression/TripleExpression.java)
    * Нельзя использовать типы `long` и `double`
    * Нельзя использовать методы классов `Math` и `StrictMath`
    * [Исходный код тестов](java/expression/exceptions/ExceptionsTest.java)
 * *HighLow*
    * Дополнительно реализовать унарные операции:
        * `high` — значение, у которого оставлен только самый старший
          установленный бит `high -4` равно `Integer.MIN_VALUE`;
        * `low` — значение, у которого оставлен только самый младший
          установленный бит `low 18` равно `2`.
    * [Исходный код тестов](java/expression/exceptions/ExceptionsHighLowTest.java)
