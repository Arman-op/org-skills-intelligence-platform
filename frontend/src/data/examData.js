// Job roles employees can target
export const JOB_ROLES = [
  { id: 'frontend', label: 'Frontend Developer', icon: '🎨' },
  { id: 'backend', label: 'Backend Developer', icon: '⚙️' },
  { id: 'data_scientist', label: 'Data Scientist', icon: '📊' },
  { id: 'devops', label: 'DevOps Engineer', icon: '🚀' },
  { id: 'cybersecurity', label: 'Cybersecurity Analyst', icon: '🔐' },
  { id: 'product_manager', label: 'Product Manager', icon: '📋' },
  { id: 'ml_engineer', label: 'ML Engineer', icon: '🤖' },
  { id: 'cloud_architect', label: 'Cloud Architect', icon: '☁️' },
];

// Skill domains per role with exam questions
export const ROLE_EXAMS = {
  frontend: {
    title: 'Frontend Developer Assessment',
    skills: ['HTML/CSS', 'JavaScript', 'React', 'Performance', 'Accessibility'],
    questions: [
      {
        id: 1, skill: 'HTML/CSS',
        question: 'Which CSS property controls the stacking order of positioned elements?',
        options: ['z-index', 'position', 'order', 'float'],
        correct: 0,
      },
      {
        id: 2, skill: 'HTML/CSS',
        question: 'What does "Flexbox" primarily solve?',
        options: ['Database queries', 'One-dimensional layout alignment', 'Authentication flows', 'API integration'],
        correct: 1,
      },
      {
        id: 3, skill: 'JavaScript',
        question: 'What is the output of: typeof null?',
        options: ['"null"', '"undefined"', '"object"', '"boolean"'],
        correct: 2,
      },
      {
        id: 4, skill: 'JavaScript',
        question: 'Which method creates a new array with results of calling a function on every element?',
        options: ['forEach', 'filter', 'map', 'reduce'],
        correct: 2,
      },
      {
        id: 5, skill: 'React',
        question: 'What hook is used to perform side effects in React functional components?',
        options: ['useState', 'useEffect', 'useContext', 'useReducer'],
        correct: 1,
      },
      {
        id: 6, skill: 'React',
        question: 'What does the virtual DOM in React do?',
        options: [
          'Directly manipulates the browser DOM',
          'Stores component state globally',
          'Creates a lightweight copy of the DOM to diff and batch updates',
          'Handles HTTP requests',
        ],
        correct: 2,
      },
      {
        id: 7, skill: 'Performance',
        question: 'Which technique defers loading of non-critical resources?',
        options: ['Tree shaking', 'Lazy loading', 'Minification', 'Transpilation'],
        correct: 1,
      },
      {
        id: 8, skill: 'Performance',
        question: 'What does LCP stand for in Core Web Vitals?',
        options: ['Largest Contentful Paint', 'Lowest Cycle Performance', 'Layout Cumulative Paint', 'Load Completion Progress'],
        correct: 0,
      },
      {
        id: 9, skill: 'Accessibility',
        question: 'Which ARIA attribute provides a text label for an element without visible text?',
        options: ['aria-label', 'aria-hidden', 'aria-live', 'aria-role'],
        correct: 0,
      },
      {
        id: 10, skill: 'Accessibility',
        question: 'What is the minimum color contrast ratio for normal text per WCAG AA?',
        options: ['2.5:1', '3:1', '4.5:1', '7:1'],
        correct: 2,
      },
    ],
  },
  backend: {
    title: 'Backend Developer Assessment',
    skills: ['APIs', 'Databases', 'Security', 'System Design', 'Cloud'],
    questions: [
      {
        id: 1, skill: 'APIs',
        question: 'Which HTTP method should be used to update a partial resource?',
        options: ['PUT', 'POST', 'PATCH', 'DELETE'],
        correct: 2,
      },
      {
        id: 2, skill: 'APIs',
        question: 'What does REST stand for?',
        options: ['Remote Endpoint State Transfer', 'Representational State Transfer', 'Relational Entity Storage Type', 'Resource Endpoint Specification Tool'],
        correct: 1,
      },
      {
        id: 3, skill: 'Databases',
        question: 'Which SQL clause is used to filter groups after aggregation?',
        options: ['WHERE', 'HAVING', 'GROUP BY', 'ORDER BY'],
        correct: 1,
      },
      {
        id: 4, skill: 'Databases',
        question: 'What does ACID stand for in database transactions?',
        options: [
          'Atomicity, Consistency, Isolation, Durability',
          'Async, Cached, Indexed, Durable',
          'Atomic, Concurrent, Integrated, Distributed',
          'All Consistent Integrated Data',
        ],
        correct: 0,
      },
      {
        id: 5, skill: 'Security',
        question: 'What type of attack inserts malicious SQL into a query?',
        options: ['XSS', 'CSRF', 'SQL Injection', 'Man-in-the-Middle'],
        correct: 2,
      },
      {
        id: 6, skill: 'Security',
        question: 'JWT stands for?',
        options: ['JavaScript Web Token', 'JSON Web Token', 'Java Web Transfer', 'Java Web Token'],
        correct: 1,
      },
      {
        id: 7, skill: 'System Design',
        question: 'What is a load balancer primarily used for?',
        options: ['Encrypting data', 'Distributing traffic across servers', 'Storing large files', 'Caching database queries'],
        correct: 1,
      },
      {
        id: 8, skill: 'System Design',
        question: 'Which pattern separates read and write models?',
        options: ['MVC', 'CQRS', 'Singleton', 'Observer'],
        correct: 1,
      },
      {
        id: 9, skill: 'Cloud',
        question: 'Which AWS service is used for serverless compute?',
        options: ['EC2', 'S3', 'Lambda', 'RDS'],
        correct: 2,
      },
      {
        id: 10, skill: 'Cloud',
        question: 'What does CDN stand for?',
        options: ['Central Data Network', 'Content Delivery Network', 'Cloud Distribution Node', 'Core Data Namespace'],
        correct: 1,
      },
    ],
  },
  data_scientist: {
    title: 'Data Scientist Assessment',
    skills: ['Statistics', 'Python/ML', 'Data Wrangling', 'Visualization', 'Deep Learning'],
    questions: [
      {
        id: 1, skill: 'Statistics',
        question: 'What does p-value < 0.05 mean in hypothesis testing?',
        options: [
          'The null hypothesis is definitely true',
          'There is statistically significant evidence to reject the null hypothesis',
          'The result occurred by chance 95% of the time',
          'The sample size is too small',
        ],
        correct: 1,
      },
      {
        id: 2, skill: 'Statistics',
        question: 'What is the difference between variance and standard deviation?',
        options: [
          'They are the same thing',
          'Standard deviation is the square root of variance',
          'Variance is the square root of standard deviation',
          'Standard deviation is variance multiplied by N',
        ],
        correct: 1,
      },
      {
        id: 3, skill: 'Python/ML',
        question: 'Which algorithm is best suited for classification with highly imbalanced classes?',
        options: ['Linear Regression', 'K-Means', 'Random Forest with class_weight', 'PCA'],
        correct: 2,
      },
      {
        id: 4, skill: 'Python/ML',
        question: 'What does cross-validation prevent?',
        options: ['Underfitting', 'Data leakage', 'Overfitting / selection bias', 'Dimensionality curse'],
        correct: 2,
      },
      {
        id: 5, skill: 'Data Wrangling',
        question: 'Which pandas method fills missing values with a forward fill?',
        options: ['fillna()', 'ffill()', 'dropna()', 'interpolate()'],
        correct: 1,
      },
      {
        id: 6, skill: 'Data Wrangling',
        question: 'What is one-hot encoding used for?',
        options: [
          'Normalizing numerical features',
          'Reducing dimensionality',
          'Converting categorical variables into binary vectors',
          'Handling imbalanced datasets',
        ],
        correct: 2,
      },
      {
        id: 7, skill: 'Visualization',
        question: 'Which chart type best shows distribution of a continuous variable?',
        options: ['Pie Chart', 'Bar Chart', 'Histogram', 'Scatter Plot'],
        correct: 2,
      },
      {
        id: 8, skill: 'Visualization',
        question: 'What does a correlation heatmap show?',
        options: [
          'Distribution of individual variables',
          'Relationships between pairs of variables',
          'Time series trends',
          'Cluster assignments',
        ],
        correct: 1,
      },
      {
        id: 9, skill: 'Deep Learning',
        question: 'What is the purpose of the activation function in a neural network?',
        options: [
          'To normalize input data',
          'To introduce non-linearity',
          'To reduce learning rate',
          'To initialize weights',
        ],
        correct: 1,
      },
      {
        id: 10, skill: 'Deep Learning',
        question: 'What is "dropout" in neural networks?',
        options: [
          'Removing outlier data points',
          'A regularization technique that randomly disables neurons during training',
          'Reducing the learning rate',
          'Removing layers from the model',
        ],
        correct: 1,
      },
    ],
  },
  devops: {
    title: 'DevOps Engineer Assessment',
    skills: ['CI/CD', 'Containers', 'Infrastructure', 'Monitoring', 'Security'],
    questions: [
      {
        id: 1, skill: 'CI/CD',
        question: 'What is the main goal of Continuous Integration?',
        options: [
          'Deploy to production automatically',
          'Merge code frequently and detect issues early with automated builds/tests',
          'Monitor production metrics',
          'Scan for security vulnerabilities',
        ],
        correct: 1,
      },
      {
        id: 2, skill: 'CI/CD',
        question: 'Which tool uses a Jenkinsfile to define pipelines?',
        options: ['GitHub Actions', 'Jenkins', 'CircleCI', 'ArgoCD'],
        correct: 1,
      },
      {
        id: 3, skill: 'Containers',
        question: 'What is a Docker layer?',
        options: [
          'A network configuration',
          'A read-only filesystem change built during image creation',
          'A running process inside a container',
          'A volume mount point',
        ],
        correct: 1,
      },
      {
        id: 4, skill: 'Containers',
        question: 'What does kubectl apply -f do in Kubernetes?',
        options: [
          'Deletes all resources',
          'Applies a configuration file to create/update resources',
          'Starts the Kubernetes dashboard',
          'Scales a deployment',
        ],
        correct: 1,
      },
      {
        id: 5, skill: 'Infrastructure',
        question: 'What is Infrastructure as Code (IaC)?',
        options: [
          'Writing code inside infrastructure tools',
          'Managing and provisioning infrastructure through machine-readable config files',
          'Monitoring code performance',
          'Writing code that runs on bare-metal servers',
        ],
        correct: 1,
      },
      {
        id: 6, skill: 'Infrastructure',
        question: 'Which tool is primarily used for IaC on multi-cloud environments?',
        options: ['Ansible', 'Terraform', 'Puppet', 'Chef'],
        correct: 1,
      },
      {
        id: 7, skill: 'Monitoring',
        question: 'What does MTTR stand for?',
        options: ['Maximum Time To Respond', 'Mean Time To Recover', 'Minimum Test To Run', 'Monitoring Threshold To Report'],
        correct: 1,
      },
      {
        id: 8, skill: 'Monitoring',
        question: 'Which tool is commonly paired with Prometheus for visualization?',
        options: ['Kibana', 'Grafana', 'Datadog', 'Splunk'],
        correct: 1,
      },
      {
        id: 9, skill: 'Security',
        question: 'What is the principle of least privilege?',
        options: [
          'Grant all users admin access by default',
          'Give users only the permissions they need to perform their job',
          'Use the cheapest security solution',
          'Allow read-only access to all resources',
        ],
        correct: 1,
      },
      {
        id: 10, skill: 'Security',
        question: 'What does SAST stand for in DevSecOps?',
        options: ['Server-side Application Security Testing', 'Static Application Security Testing', 'Secure Authentication System Tool', 'System Access Security Token'],
        correct: 1,
      },
    ],
  },
  cybersecurity: {
    title: 'Cybersecurity Analyst Assessment',
    skills: ['Threats', 'Network Security', 'Cryptography', 'Incident Response', 'Compliance'],
    questions: [
      { id: 1, skill: 'Threats', question: 'What type of malware encrypts files and demands payment?', options: ['Spyware', 'Ransomware', 'Adware', 'Rootkit'], correct: 1 },
      { id: 2, skill: 'Threats', question: 'What is a zero-day vulnerability?', options: ['A bug fixed within 24 hours', 'An unknown vulnerability with no patch available', 'A vulnerability discovered on Monday', 'A vulnerability in zero-trust systems'], correct: 1 },
      { id: 3, skill: 'Network Security', question: 'What does a firewall do?', options: ['Encrypts data at rest', 'Monitors and controls incoming/outgoing network traffic', 'Backs up data', 'Manages user logins'], correct: 1 },
      { id: 4, skill: 'Network Security', question: 'What is ARP poisoning?', options: ['A physical attack on network cable', 'An attack that associates attacker MAC with legitimate IP', 'Flooding a server with requests', 'Intercepting HTTPS traffic'], correct: 1 },
      { id: 5, skill: 'Cryptography', question: 'What is a symmetric encryption algorithm?', options: ['RSA', 'Diffie-Hellman', 'AES', 'ECC'], correct: 2 },
      { id: 6, skill: 'Cryptography', question: 'What is a digital certificate primarily used for?', options: ['Speeding up encryption', 'Verifying the identity of an entity', 'Storing private keys', 'Compressing data'], correct: 1 },
      { id: 7, skill: 'Incident Response', question: 'What is the first step in incident response?', options: ['Containment', 'Eradication', 'Identification/Detection', 'Recovery'], correct: 2 },
      { id: 8, skill: 'Incident Response', question: 'What is forensic imaging?', options: ['Taking screenshots of incidents', 'Creating an exact bit-for-bit copy of storage media', 'Monitoring network traffic', 'Analyzing malware behavior'], correct: 1 },
      { id: 9, skill: 'Compliance', question: 'Which regulation focuses on data protection for EU citizens?', options: ['HIPAA', 'SOC 2', 'GDPR', 'PCI-DSS'], correct: 2 },
      { id: 10, skill: 'Compliance', question: 'What does PCI-DSS ensure?', options: ['Healthcare data protection', 'Secure handling of credit card information', 'Cloud security standards', 'Government data classification'], correct: 1 },
    ],
  },
  product_manager: {
    title: 'Product Manager Assessment',
    skills: ['Strategy', 'Agile', 'Data Analytics', 'User Research', 'Roadmapping'],
    questions: [
      { id: 1, skill: 'Strategy', question: 'What is a North Star Metric?', options: ['The most vanity metric to report', 'A single metric that captures the core value delivered to users', 'Monthly revenue target', 'Number of features shipped'], correct: 1 },
      { id: 2, skill: 'Strategy', question: 'What does OKR stand for?', options: ['Objective and Key Results', 'Outcome and Key Risks', 'Output and Key Releases', 'Objectives and Knowledge Repositories'], correct: 0 },
      { id: 3, skill: 'Agile', question: 'What happens in a Sprint Retrospective?', options: ['Demo the sprint work to stakeholders', 'Plan next sprint backlog', 'Review what went well/poorly and improve processes', 'Prioritize the full backlog'], correct: 2 },
      { id: 4, skill: 'Agile', question: 'What is the purpose of user stories?', options: ['To document technical architecture', 'To describe features from the end-user perspective', 'To track bugs', 'To schedule releases'], correct: 1 },
      { id: 5, skill: 'Data Analytics', question: 'What does cohort analysis track?', options: ['Individual user journeys', 'Behavior of a group of users who share a characteristic over time', 'Total user count', 'Revenue per feature'], correct: 1 },
      { id: 6, skill: 'Data Analytics', question: 'What is A/B testing?', options: ['Testing two versions of a feature with split user groups', 'Testing on development and production environments', 'Alpha and Beta releases', 'Automated and manual testing'], correct: 0 },
      { id: 7, skill: 'User Research', question: 'What type of research gives statistical significance?', options: ['Qualitative interviews', 'Usability tests', 'Quantitative surveys', 'Focus groups'], correct: 2 },
      { id: 8, skill: 'User Research', question: 'What is a Jobs-to-be-Done framework?', options: ['A hiring process', 'Understanding underlying goals users are trying to accomplish', 'Assigning tasks to team members', 'Mapping job titles to features'], correct: 1 },
      { id: 9, skill: 'Roadmapping', question: 'What is a NOW-NEXT-LATER roadmap?', options: ['Sprints divided by time', 'A priority-based roadmap showing current, upcoming, and future work without fixed dates', 'A Gantt chart with 3 phases', 'Three release versions'], correct: 1 },
      { id: 10, skill: 'Roadmapping', question: 'What is the MoSCoW method used for?', options: ['Market research framework', 'Prioritizing requirements into Must, Should, Could, and Won\'t', 'A coding methodology', 'A meetings framework'], correct: 1 },
    ],
  },
  ml_engineer: {
    title: 'ML Engineer Assessment',
    skills: ['ML Fundamentals', 'Feature Engineering', 'Model Deployment', 'MLOps', 'Deep Learning'],
    questions: [
      { id: 1, skill: 'ML Fundamentals', question: 'What is regularization in machine learning?', options: ['Normalizing data to [0,1]', 'Adding a penalty term to prevent overfitting', 'Increasing model complexity', 'Splitting train/test data'], correct: 1 },
      { id: 2, skill: 'ML Fundamentals', question: 'What is the bias-variance tradeoff?', options: ['Tradeoff between speed and accuracy', 'Tradeoff between underfitting (high bias) and overfitting (high variance)', 'Tradeoff between precision and recall', 'Tradeoff between training and inference time'], correct: 1 },
      { id: 3, skill: 'Feature Engineering', question: 'What is feature scaling used for?', options: ['Adding more features', 'Bringing features to a comparable scale for distance-based algorithms', 'Removing outliers', 'Encoding categorical variables'], correct: 1 },
      { id: 4, skill: 'Feature Engineering', question: 'What does PCA do?', options: ['Predicts class labels', 'Reduces dimensionality while preserving variance', 'Normalizes features', 'Selects the best model'], correct: 1 },
      { id: 5, skill: 'Model Deployment', question: 'What is model serialization?', options: ['Running the model in parallel', 'Saving a trained model to disk for reuse', 'Converting model to JSON', 'Splitting model into microservices'], correct: 1 },
      { id: 6, skill: 'Model Deployment', question: 'What is a REST API in the context of ML deployment?', options: ['A type of neural network', 'An interface allowing applications to request predictions from a model', 'A database for storing models', 'A visualization tool'], correct: 1 },
      { id: 7, skill: 'MLOps', question: 'What does model drift refer to?', options: ['Moving a model between servers', 'Degradation of model performance due to changes in real-world data', 'Updating model weights', 'Version control for models'], correct: 1 },
      { id: 8, skill: 'MLOps', question: 'What is a feature store?', options: ['A marketplace for buying features', 'A centralized repository for storing and sharing ML features', 'A model registry', 'A data lake'], correct: 1 },
      { id: 9, skill: 'Deep Learning', question: 'What is the vanishing gradient problem?', options: ['Model accuracy plateaus', 'Gradients become extremely small, slowing learning in early layers', 'Gradient descent diverges', 'Loss function reaches zero too quickly'], correct: 1 },
      { id: 10, skill: 'Deep Learning', question: 'What is transfer learning?', options: ['Moving a model between cloud providers', 'Applying knowledge from a pre-trained model to a new related task', 'Transferring weights between two identical models', 'Copying training data'], correct: 1 },
    ],
  },
  cloud_architect: {
    title: 'Cloud Architect Assessment',
    skills: ['Cloud Fundamentals', 'Architecture Patterns', 'Networking', 'Cost Optimization', 'Security'],
    questions: [
      { id: 1, skill: 'Cloud Fundamentals', question: 'What is the difference between IaaS, PaaS, and SaaS?', options: ['Different pricing models', 'Different levels of managed infrastructure from hardware to full software', 'Different cloud providers', 'Different storage types'], correct: 1 },
      { id: 2, skill: 'Cloud Fundamentals', question: 'What is cloud elasticity?', options: ['Physical stretching of cloud cables', 'The ability to automatically scale resources up or down based on demand', 'Migrating between cloud providers', 'Using multiple availability zones'], correct: 1 },
      { id: 3, skill: 'Architecture Patterns', question: 'What is the strangler fig pattern?', options: ['A security attack pattern', 'Incrementally replacing a legacy system by routing traffic to new services', 'A load balancing strategy', 'A backup recovery method'], correct: 1 },
      { id: 4, skill: 'Architecture Patterns', question: 'What is a microservices architecture?', options: ['Running very small virtual machines', 'Decomposing an application into small, independently deployable services', 'Using minimal cloud resources', 'Small code functions'], correct: 1 },
      { id: 5, skill: 'Networking', question: 'What is a VPC?', options: ['Virtual Processing Core', 'Virtual Private Cloud — an isolated network environment in the cloud', 'Verified Public Certificate', 'Volume Persistence Controller'], correct: 1 },
      { id: 6, skill: 'Networking', question: 'What does a peering connection do in cloud networking?', options: ['Monitors network traffic', 'Connects two VPCs to route traffic privately', 'Provides internet access to a VPC', 'Load balances between subnets'], correct: 1 },
      { id: 7, skill: 'Cost Optimization', question: 'What are Reserved Instances in AWS?', options: ['Emergency backup instances', 'Committed usage contracts that offer significant discount over on-demand pricing', 'High-performance compute instances', 'Instances reserved for specific regions'], correct: 1 },
      { id: 8, skill: 'Cost Optimization', question: 'What is right-sizing in cloud cost optimization?', options: ['Using the largest possible instance', 'Matching instance type/size to actual workload requirements to avoid waste', 'Using the cheapest region', 'Compressing all data to reduce storage costs'], correct: 1 },
      { id: 9, skill: 'Security', question: 'What is IAM in cloud computing?', options: ['Infrastructure Asset Management', 'Identity and Access Management — controls who can access what resources', 'Integrated Application Monitoring', 'Instance Allocation Manager'], correct: 1 },
      { id: 10, skill: 'Security', question: 'What is the shared responsibility model?', options: ['All security is the cloud provider\'s responsibility', 'Security responsibilities are divided between the provider and the customer', 'All security is the customer\'s responsibility', 'Sharing security tools between organizations'], correct: 1 },
    ],
  },
};

// Courses mapped by skill domain with role associations
export const SKILL_COURSES = {
  'HTML/CSS': [
    { title: 'Modern CSS Mastery', provider: 'Scrimba', duration: '12h', level: 'Intermediate', url: 'https://scrimba.com', emoji: '🎨', matchScore: 96 },
    { title: 'CSS Grid & Flexbox Deep Dive', provider: 'Frontend Masters', duration: '8h', level: 'Intermediate', url: 'https://frontendmasters.com', emoji: '📐', matchScore: 92 },
  ],
  'JavaScript': [
    { title: 'JavaScript: The Hard Parts', provider: 'Frontend Masters', duration: '10h', level: 'Advanced', url: 'https://frontendmasters.com', emoji: '⚡', matchScore: 98 },
    { title: 'ES6+ Features Complete Guide', provider: 'Udemy', duration: '6h', level: 'Intermediate', url: 'https://udemy.com', emoji: '📜', matchScore: 90 },
  ],
  'React': [
    { title: 'React - The Complete Guide', provider: 'Udemy', duration: '40h', level: 'Beginner-Advanced', url: 'https://udemy.com', emoji: '⚛️', matchScore: 99 },
    { title: 'Epic React', provider: 'Kent C. Dodds', duration: '30h', level: 'Advanced', url: 'https://epicreact.dev', emoji: '🚀', matchScore: 97 },
  ],
  'Performance': [
    { title: 'Web Performance Fundamentals', provider: 'Frontend Masters', duration: '4h', level: 'Intermediate', url: 'https://frontendmasters.com', emoji: '⚡', matchScore: 94 },
    { title: 'Google Core Web Vitals Training', provider: 'Google', duration: '3h', level: 'Beginner', url: 'https://web.dev', emoji: '📊', matchScore: 88 },
  ],
  'Accessibility': [
    { title: 'Accessibility in JavaScript Applications', provider: 'Frontend Masters', duration: '5h', level: 'Intermediate', url: 'https://frontendmasters.com', emoji: '♿', matchScore: 91 },
    { title: 'WCAG 2.1 Complete Course', provider: 'Coursera', duration: '4h', level: 'Beginner', url: 'https://coursera.org', emoji: '🌍', matchScore: 85 },
  ],
  'APIs': [
    { title: 'REST API Design & Development', provider: 'Udemy', duration: '8h', level: 'Intermediate', url: 'https://udemy.com', emoji: '🔌', matchScore: 95 },
    { title: 'API Design Patterns', provider: 'O\'Reilly Learning', duration: '6h', level: 'Advanced', url: 'https://oreilly.com', emoji: '📡', matchScore: 90 },
  ],
  'Databases': [
    { title: 'SQL & Database Design A-Z', provider: 'Udemy', duration: '12h', level: 'Beginner-Advanced', url: 'https://udemy.com', emoji: '🗄️', matchScore: 97 },
    { title: 'NoSQL Database Fundamentals', provider: 'MongoDB University', duration: '5h', level: 'Beginner', url: 'https://university.mongodb.com', emoji: '📦', matchScore: 88 },
  ],
  'Security': [
    { title: 'CompTIA Security+ Prep', provider: 'Coursera', duration: '20h', level: 'Intermediate', url: 'https://coursera.org', emoji: '🔐', matchScore: 96 },
    { title: 'OWASP Top 10 Vulnerabilities', provider: 'Pluralsight', duration: '4h', level: 'Intermediate', url: 'https://pluralsight.com', emoji: '🛡️', matchScore: 92 },
  ],
  'System Design': [
    { title: 'Grokking System Design', provider: 'Educative.io', duration: '15h', level: 'Advanced', url: 'https://educative.io', emoji: '🏗️', matchScore: 99 },
    { title: 'System Design Interview Guide', provider: 'ByteByteGo', duration: '10h', level: 'Advanced', url: 'https://bytebytego.com', emoji: '📐', matchScore: 95 },
  ],
  'Cloud': [
    { title: 'AWS Certified Solutions Architect', provider: 'A Cloud Guru', duration: '40h', level: 'Intermediate', url: 'https://acloudguru.com', emoji: '☁️', matchScore: 98 },
    { title: 'Azure Fundamentals AZ-900', provider: 'Microsoft Learn', duration: '10h', level: 'Beginner', url: 'https://learn.microsoft.com', emoji: '🔷', matchScore: 90 },
  ],
  'Statistics': [
    { title: 'Statistics for Data Science', provider: 'Coursera', duration: '15h', level: 'Beginner', url: 'https://coursera.org', emoji: '📈', matchScore: 95 },
    { title: 'Bayesian Statistics', provider: 'edX', duration: '12h', level: 'Advanced', url: 'https://edx.org', emoji: '🔢', matchScore: 88 },
  ],
  'Python/ML': [
    { title: 'Machine Learning by Andrew Ng', provider: 'Coursera', duration: '60h', level: 'Intermediate', url: 'https://coursera.org', emoji: '🐍', matchScore: 99 },
    { title: 'Scikit-Learn & ML with Python', provider: 'Udemy', duration: '20h', level: 'Intermediate', url: 'https://udemy.com', emoji: '🤖', matchScore: 94 },
  ],
  'Data Wrangling': [
    { title: 'Pandas for Data Analysis', provider: 'Kaggle Learn', duration: '5h', level: 'Beginner', url: 'https://kaggle.com', emoji: '🐼', matchScore: 92 },
    { title: 'PySpark & Big Data Processing', provider: 'Databricks', duration: '12h', level: 'Advanced', url: 'https://databricks.com', emoji: '⚡', matchScore: 89 },
  ],
  'Visualization': [
    { title: 'Data Visualization with Python', provider: 'DataCamp', duration: '8h', level: 'Intermediate', url: 'https://datacamp.com', emoji: '📊', matchScore: 93 },
    { title: 'Tableau Desktop Specialist', provider: 'Tableau', duration: '15h', level: 'Beginner', url: 'https://tableau.com', emoji: '🎨', matchScore: 91 },
  ],
  'Deep Learning': [
    { title: 'Deep Learning Specialization', provider: 'Coursera (deeplearning.ai)', duration: '80h', level: 'Advanced', url: 'https://coursera.org', emoji: '🧠', matchScore: 99 },
    { title: 'PyTorch for Deep Learning', provider: 'fast.ai', duration: '30h', level: 'Intermediate', url: 'https://fast.ai', emoji: '🔥', matchScore: 96 },
  ],
  'CI/CD': [
    { title: 'GitHub Actions - The Complete Guide', provider: 'Udemy', duration: '10h', level: 'Intermediate', url: 'https://udemy.com', emoji: '🔄', matchScore: 94 },
    { title: 'GitLab CI/CD Pipelines', provider: 'Pluralsight', duration: '8h', level: 'Intermediate', url: 'https://pluralsight.com', emoji: '⚙️', matchScore: 90 },
  ],
  'Containers': [
    { title: 'Docker & Kubernetes Complete Guide', provider: 'Udemy', duration: '22h', level: 'Beginner-Advanced', url: 'https://udemy.com', emoji: '🐳', matchScore: 98 },
    { title: 'Kubernetes for Developers', provider: 'Linux Foundation', duration: '15h', level: 'Advanced', url: 'https://training.linuxfoundation.org', emoji: '⛵', matchScore: 93 },
  ],
  'Infrastructure': [
    { title: 'Terraform - From Beginner to Expert', provider: 'Udemy', duration: '12h', level: 'Intermediate', url: 'https://udemy.com', emoji: '🏗️', matchScore: 96 },
    { title: 'Ansible for Automation', provider: 'Red Hat', duration: '8h', level: 'Intermediate', url: 'https://redhat.com', emoji: '🔧', matchScore: 91 },
  ],
  'Monitoring': [
    { title: 'Prometheus & Grafana Observability', provider: 'Udemy', duration: '8h', level: 'Intermediate', url: 'https://udemy.com', emoji: '📡', matchScore: 93 },
    { title: 'ELK Stack Mastery', provider: 'Elastic', duration: '12h', level: 'Advanced', url: 'https://elastic.co', emoji: '📊', matchScore: 89 },
  ],
  'Threats': [
    { title: 'Cybersecurity Threats & Attack Vectors', provider: 'Coursera', duration: '10h', level: 'Beginner', url: 'https://coursera.org', emoji: '⚠️', matchScore: 95 },
    { title: 'CEH - Certified Ethical Hacker', provider: 'EC-Council', duration: '40h', level: 'Advanced', url: 'https://eccouncil.org', emoji: '🎯', matchScore: 92 },
  ],
  'Network Security': [
    { title: 'CompTIA Network+ Certification', provider: 'CompTIA', duration: '25h', level: 'Intermediate', url: 'https://comptia.org', emoji: '🔒', matchScore: 96 },
    { title: 'Wireshark: Network Analysis', provider: 'Udemy', duration: '6h', level: 'Intermediate', url: 'https://udemy.com', emoji: '🌐', matchScore: 88 },
  ],
  'Cryptography': [
    { title: 'Cryptography I by Dan Boneh', provider: 'Coursera', duration: '23h', level: 'Advanced', url: 'https://coursera.org', emoji: '🔑', matchScore: 98 },
    { title: 'Applied Cryptography', provider: 'Udacity', duration: '15h', level: 'Intermediate', url: 'https://udacity.com', emoji: '🗝️', matchScore: 90 },
  ],
  'Incident Response': [
    { title: 'Digital Forensics & Incident Response', provider: 'SANS', duration: '30h', level: 'Advanced', url: 'https://sans.org', emoji: '🔍', matchScore: 97 },
    { title: 'Incident Handling Fundamentals', provider: 'Cybrary', duration: '10h', level: 'Beginner', url: 'https://cybrary.it', emoji: '🚨', matchScore: 91 },
  ],
  'Compliance': [
    { title: 'GDPR, HIPAA & Cybersecurity Compliance', provider: 'Coursera', duration: '8h', level: 'Beginner', url: 'https://coursera.org', emoji: '📋', matchScore: 93 },
    { title: 'ISO 27001 Information Security', provider: 'BSI', duration: '10h', level: 'Intermediate', url: 'https://bsigroup.com', emoji: '✅', matchScore: 88 },
  ],
  'Strategy': [
    { title: 'Product Strategy & Roadmapping', provider: 'Product School', duration: '8h', level: 'Intermediate', url: 'https://productschool.com', emoji: '🎯', matchScore: 95 },
    { title: 'Blue Ocean Strategy', provider: 'Coursera', duration: '10h', level: 'Advanced', url: 'https://coursera.org', emoji: '🌊', matchScore: 88 },
  ],
  'Agile': [
    { title: 'Agile Project Management', provider: 'Google (Coursera)', duration: '20h', level: 'Beginner', url: 'https://coursera.org', emoji: '🏃', matchScore: 96 },
    { title: 'Certified Scrum Master Prep', provider: 'Scrum.org', duration: '15h', level: 'Intermediate', url: 'https://scrum.org', emoji: '♟️', matchScore: 93 },
  ],
  'Data Analytics': [
    { title: 'Google Data Analytics Certificate', provider: 'Coursera', duration: '45h', level: 'Beginner', url: 'https://coursera.org', emoji: '📈', matchScore: 97 },
    { title: 'Product Analytics with Mixpanel', provider: 'Mixpanel', duration: '6h', level: 'Intermediate', url: 'https://mixpanel.com', emoji: '🔢', matchScore: 90 },
  ],
  'User Research': [
    { title: 'UX Research at Scale', provider: 'Nielsen Norman Group', duration: '10h', level: 'Advanced', url: 'https://nngroup.com', emoji: '🔬', matchScore: 95 },
    { title: 'Conducting Usability Testing', provider: 'Interaction Design Foundation', duration: '8h', level: 'Beginner', url: 'https://interaction-design.org', emoji: '👤', matchScore: 89 },
  ],
  'Roadmapping': [
    { title: 'Product Roadmapping That Works', provider: 'Product School', duration: '5h', level: 'Intermediate', url: 'https://productschool.com', emoji: '🗺️', matchScore: 93 },
    { title: 'Prioritization Frameworks', provider: 'Reforge', duration: '6h', level: 'Advanced', url: 'https://reforge.com', emoji: '🎚️', matchScore: 90 },
  ],
  'ML Fundamentals': [
    { title: 'Intro to ML (MITx)', provider: 'edX', duration: '15h', level: 'Intermediate', url: 'https://edx.org', emoji: '🔬', matchScore: 97 },
    { title: 'ML Engineering for Production', provider: 'Coursera', duration: '25h', level: 'Advanced', url: 'https://coursera.org', emoji: '⚙️', matchScore: 94 },
  ],
  'Feature Engineering': [
    { title: 'Feature Engineering for ML', provider: 'Kaggle Learn', duration: '5h', level: 'Intermediate', url: 'https://kaggle.com', emoji: '🛠️', matchScore: 93 },
    { title: 'Advanced Feature Engineering', provider: 'DataCamp', duration: '8h', level: 'Advanced', url: 'https://datacamp.com', emoji: '✨', matchScore: 90 },
  ],
  'Model Deployment': [
    { title: 'Deploying ML Models with Flask & Docker', provider: 'Udemy', duration: '10h', level: 'Intermediate', url: 'https://udemy.com', emoji: '🚀', matchScore: 95 },
    { title: 'ML Model Serving with FastAPI', provider: 'Coursera', duration: '8h', level: 'Advanced', url: 'https://coursera.org', emoji: '⚡', matchScore: 92 },
  ],
  'MLOps': [
    { title: 'MLOps Specialization', provider: 'Coursera (deeplearning.ai)', duration: '35h', level: 'Advanced', url: 'https://coursera.org', emoji: '🔁', matchScore: 99 },
    { title: 'MLflow for ML Lifecycle', provider: 'Databricks', duration: '6h', level: 'Intermediate', url: 'https://databricks.com', emoji: '📦', matchScore: 91 },
  ],
  'Cloud Fundamentals': [
    { title: 'AWS Cloud Practitioner', provider: 'AWS Training', duration: '12h', level: 'Beginner', url: 'https://aws.training', emoji: '☁️', matchScore: 96 },
    { title: 'Google Cloud Fundamentals', provider: 'Google Cloud', duration: '10h', level: 'Beginner', url: 'https://cloud.google.com', emoji: '🔵', matchScore: 91 },
  ],
  'Architecture Patterns': [
    { title: 'Cloud Architecture Patterns', provider: 'O\'Reilly', duration: '15h', level: 'Advanced', url: 'https://oreilly.com', emoji: '🏛️', matchScore: 97 },
    { title: 'Microservices Design Patterns', provider: 'Pluralsight', duration: '12h', level: 'Advanced', url: 'https://pluralsight.com', emoji: '🔗', matchScore: 94 },
  ],
  'Networking': [
    { title: 'AWS Networking & VPCs', provider: 'A Cloud Guru', duration: '10h', level: 'Intermediate', url: 'https://acloudguru.com', emoji: '🌐', matchScore: 95 },
    { title: 'Network Engineering Fundamentals', provider: 'Cisco NetAcad', duration: '20h', level: 'Beginner', url: 'https://netacad.com', emoji: '📡', matchScore: 88 },
  ],
  'Cost Optimization': [
    { title: 'AWS Cost Optimization', provider: 'AWS Training', duration: '6h', level: 'Intermediate', url: 'https://aws.training', emoji: '💰', matchScore: 93 },
    { title: 'FinOps: Cloud Financial Management', provider: 'FinOps Foundation', duration: '8h', level: 'Intermediate', url: 'https://finops.org', emoji: '📊', matchScore: 90 },
  ],
};
