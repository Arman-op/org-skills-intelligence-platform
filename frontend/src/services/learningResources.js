export const buildLearningResources = (skill) => ([
  {
    title: `GeeksforGeeks: ${skill}`,
    provider: 'GeeksforGeeks',
    url: `https://www.geeksforgeeks.org/search/?q=${encodeURIComponent(skill)}`,
    type: 'Practice + articles',
    duration: 'Self-paced',
    emoji: '🧠',
  },
  {
    title: `Infosys Springboard: ${skill}`,
    provider: 'Infosys Springboard',
    url: `https://infyspringboard.onwingspan.com/web/en/page/home`,
    type: 'Structured learning',
    duration: 'Self-paced',
    emoji: '🎓',
  },
  {
    title: `MDN / Official docs: ${skill}`,
    provider: 'MDN',
    url: `https://developer.mozilla.org/en-US/search?q=${encodeURIComponent(skill)}`,
    type: 'Reference docs',
    duration: 'Reference',
    emoji: '📘',
  },
]);