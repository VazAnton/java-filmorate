# java-filmorate
Template repository for Filmorate project.
![Схема БД приложения](![img.png](img.png))
Задачи по развитию проекта были разделены внутри команды.
Спиок реализованных во время группового проекта фич:
 - add-remove-endpoint - Ввели возможность удаления фильма и пользователя по уникальному номеру.
Ответственный за реализацию:
VazAnton
- add-director - Ввели возможность добавления, обновления, получения информации о режиссёре, а также получения списка 
фильмов выбранного режиссёра, в том числе отсортированных по году выпуска фильма.
Ответственный за реализацию:
VazAnton
- add-common-films - Реализовали возможность вывода общих с другом фильмов с сортировкой по их популярности
Ответственный за реализацию:
AleksandraPalagina
- add-most-populars - Добавили возможность выводить топ-N фильмов по количеству лайков
Ответственный за реализацию:
anastasiaelis
- add-feed - Добавили возможность просмотра последних событий на платформе — добавление в друзья, удаление из друзей, 
лайки и отзывы, которые оставили друзья пользователя.
Ответственный за реализацию:
ODmain
- add-recommendations - Реализовать простую рекомендательную систему для фильмов согласно предложенному алгоритму:
1. Найти пользователей с максимальным количеством пересечения по лайкам.
2. Определить фильмы, которые один пролайкал, а другой нет.
3. Рекомендовать фильмы, которым поставил лайк пользователь с похожими вкусами, а тот, для кого составляется рекомендация, 
ещё не поставил.
Ответственный за реализацию:
AleksandraPalagina
- add-search - реализовали возможность поиска по названию фильмов и по режиссёру (если вы решили добавить информацию о 
режиссёрах).
Алгоритм умеет искать по подстроке. Например, вы вводите «крад», а в поиске возвращаются следующие фильмы: 
«Крадущийся тигр, затаившийся дракон», «Крадущийся в ночи» и другие.
Ответственный за реализацию:
Eugeny Burovnikov
- add-reviews - В приложении появилась возможность оставлять отзывы на фильмы. Добавленные отзывы имеют рейтинг и 
несколько дополнительных характеристик.
Характеристики отзыва:
1. Оценка — полезно/бесполезно.
2. Тип отзыва — негативный/положительный.
Рейтинг отзыва - У отзыва имеется рейтинг. При создании отзыва рейтинг равен нулю. Если пользователь оценил отзыв как 
полезный, это увеличивает его рейтинг на 1. Если как бесполезный, то уменьшает на 1.
3. Отзывы должны сортироваться по рейтингу полезности.
