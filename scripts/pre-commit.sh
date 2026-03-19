#!/bin/bash
echo "🚀 Запуск статического анализа (Detekt)..."

./gradlew :composeApp:detekt

EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo "❌ Detekt нашел ошибки. Исправьте их перед коммитом."
    exit $EXIT_CODE
fi

echo "✅ Проверка пройдена!"