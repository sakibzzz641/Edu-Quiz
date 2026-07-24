# Edu-Quiz — সব ফরম্যাট এক জায়গায় (Quick Reference)

## ১. GitHub ফোল্ডার স্ট্রাকচার (পুরো ছবি)

```
asset/
├── index.json              ← কুইজ ক্যাটাগরির লিস্ট + version
├── english.json             ← English এর সব প্রশ্ন
├── science.json             ← Science এর সব প্রশ্ন
├── study_index.json         ← স্টাডি কনটেন্টের লিস্ট + version
└── study/
    ├── English/
    │   ├── content.md               (top-level overview)
    │   └── Grammar/
    │       ├── content.md            (mid-level overview)
    │       └── Parts_of_Speech/
    │           ├── content.md        (mid-level overview)
    │           ├── Noun.md           (leaf-level detail)
    │           └── Verb.md           (leaf-level detail)
    └── Science/
        └── content.md
```

**নিয়ম:**
- ফাইল/ফোল্ডারের নামে স্পেস না দিয়ে `_` (underscore) ব্যবহার করবেন (`Parts_of_Speech`)
- প্রতিটা লেভেলের general ব্যাখ্যা থাকলে ফাইলের নাম সবসময় `content.md`
- leaf topic এর জন্য নিজের নামে ফাইল (`Noun.md`, `Verb.md`)

---

## ২. Quiz প্রশ্নের ফরম্যাট

### প্রতিটা ক্যাটাগরির JSON ফাইল (যেমন `english.json`)

```json
{
  "category": "English",
  "version": 1,
  "questions": [
    {
      "id": "eng_0001",
      "category_path": "English > Grammar > Parts of Speech > Noun",
      "question": "নিচের কোনটি Noun?",
      "options": ["Run", "Beautiful", "Dhaka", "Quickly"],
      "correct_answer_index": 2,
      "explanation": "Dhaka একটি শহরের নাম, তাই এটি Proper Noun।"
    }
  ]
}
```

| ফিল্ড | বর্ণনা |
|---|---|
| `category` | টপ-লেভেল ক্যাটাগরির নাম |
| `version` | এই ফাইলের ভার্সন — কনটেন্ট বদলালে ১ বাড়াতে হবে |
| `id` | ইউনিক আইডি, ফরম্যাট: `<prefix>_<4-digit>` (যেমন eng_0001) |
| `category_path` | পূর্ণ nested path, `>` দিয়ে আলাদা |
| `options` | ঠিক ৪টা অপশন |
| `correct_answer_index` | 0 থেকে শুরু (0,1,2,3) |
| `explanation` | ১-২ বাক্যে ব্যাখ্যা |

### `index.json` (কুইজ ক্যাটাগরির মাস্টার লিস্ট)

```json
{
  "categories": [
    { "name": "English", "file": "english.json", "version": 1 },
    { "name": "Science", "file": "science.json", "version": 1 }
  ]
}
```

---

## ৩. Study Content (📖 পড়ুন) ফরম্যাট

### `study_index.json` (স্টাডি কনটেন্টের মাস্টার লিস্ট)

```json
{
  "topics": [
    { "category_path": "English", "file": "study/English/content.md", "version": 1 },
    { "category_path": "English > Grammar", "file": "study/English/Grammar/content.md", "version": 1 },
    { "category_path": "English > Grammar > Parts of Speech > Noun", "file": "study/English/Grammar/Parts_of_Speech/Noun.md", "version": 1 }
  ]
}
```

| ফিল্ড | বর্ণনা |
|---|---|
| `category_path` | যেকোনো লেভেলের পূর্ণ path (top-level, mid-level, বা leaf যেকোনোটাই হতে পারে) |
| `file` | `.md` ফাইলের path, রিপোর root থেকে |
| `version` | কনটেন্ট বদলালে বাড়াতে হবে |

### `.md` ফাইলের ভিতরের ফরম্যাট (Markdown)

```markdown
# টাইটেল (মূল হেডিং)

সংক্ষিপ্ত ভূমিকা বাক্য।

## সাব-সেকশন হেডিং

- **গুরুত্বপূর্ণ শব্দ bold** করে বুলেট পয়েন্ট
- আরেকটা পয়েন্ট

## টেবিল দরকার হলে

| কলাম ১ | কলাম ২ |
|---|---|
| ডেটা | ডেটা |

> **মনে রাখবেন:** জরুরি নোট বা টিপস এভাবে blockquote এ লিখবেন।
```

**নিয়ম:**
- `#` = মূল টাইটেল, `##` = সাব-সেকশন
- **bold** দিয়ে গুরুত্বপূর্ণ শব্দ হাইলাইট
- `-` দিয়ে bullet list
- `|` দিয়ে table (তুলনামূলক তথ্যের জন্য)
- `>` দিয়ে blockquote (জরুরি নোট/টিপস)
- কোনো ছবি/ডায়াগ্রাম রাখা হবে না

---

## ৪. AI কে বলার সময় যা বলবেন (সংক্ষিপ্ত সারাংশ)

**নতুন Quiz প্রশ্ন বানাতে:** "এই [টেক্সট/ছবি] থেকে English > Grammar > Parts of Speech > Noun ক্যাটাগরির জন্য উপরের quiz JSON ফরম্যাটে প্রশ্ন বানিয়ে দাও, prefix `eng`।"

**নতুন Study content বানাতে:** "এই নোট থেকে English > Grammar > Parts of Speech > Noun টপিকের জন্য উপরের Markdown ফরম্যাটে স্টাডি নোট বানিয়ে দাও, ছবি ছাড়া, table/bullet/blockquote ব্যবহার করে।"

**নতুন ক্যাটাগরি/টপিক অ্যাপে যোগ করতে (কোড পরিবর্তনের জন্য AI কোডিং টুলকে):** "নতুন ফাইল history.json এবং study/History/content.md যোগ করেছি, index.json ও study_index.json এ entry যোগ করেছি — sync করে চেক করো ঠিকমতো লোড হচ্ছে কিনা।"

---

## ৫. প্রতিবার নতুন কনটেন্ট যোগ করার Checklist

- [ ] নতুন `.json` (quiz) বা `.md` (study) ফাইল বানানো, সঠিক ফোল্ডারে
- [ ] `index.json` বা `study_index.json` এ entry যোগ করা
- [ ] যদি পুরনো ফাইল **edit** করেন (নতুন ফাইল না বানিয়ে), তাহলে সেই এন্ট্রির `version` নাম্বার ১ বাড়ানো — এটা ভুলে গেলে অ্যাপ আপডেট বুঝবে না
- [ ] GitHub এ commit করা
- [ ] অ্যাপে sync করে যাচাই করা
