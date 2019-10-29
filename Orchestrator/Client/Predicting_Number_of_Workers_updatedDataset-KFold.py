#!/usr/bin/env python
# coding: utf-8

# In[1]:


from __future__ import print_function
from __future__ import division

get_ipython().run_line_magic('matplotlib', 'inline')

import pandas as pd
import numpy as np
import seaborn as sns
import statsmodels.api as sm
import xgboost as xgb

from sklearn import tree
from sklearn.metrics import accuracy_score 
from sklearn.ensemble import RandomForestRegressor
from matplotlib import pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.ensemble import GradientBoostingClassifier, RandomForestClassifier
from sklearn.model_selection import KFold
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import GridSearchCV
from sklearn.model_selection import StratifiedKFold
from sklearn.datasets import load_digits
from sklearn.model_selection import cross_val_score
from sklearn import preprocessing

# just for the sake of this blog post!
from warnings import filterwarnings
filterwarnings('ignore')
digits = load_digits()


# In[2]:

print 'Winmaaaaaaa'
df =pd.read_csv('./feature_set.csv')


# In[3]:


def preprocess_data(data_path, labels_path=None):
    df = pd.read_csv(data_path)
    
    # select features we want
    features = ['Number_of_Workers',
                'Number_of_partial_siddhi_apps',
                'Integer operators',
                'Aggregation function'
               ]
    df = df[features]
# fill missing values
    df.fillna(method='ffill', inplace=True)

    # add labels to dataframe
    if labels_path:
        labels = pd.read_csv(labels_path)
        return df,labels
        #df = df.join(labels)
    
    return df,labels


# In[4]:


df,labels = preprocess_data('./feature_set_normalized.csv','./labels.csv')


# In[5]:


#df=preprocessing.scale(df)


# In[6]:


#labels=preprocessing.scale(labels)


# In[7]:


df.shape


# In[8]:


X_train, X_test, Y_train, Y_test = train_test_split(df, labels, test_size=0.2, random_state=40)


# In[9]:


X_test.shape


# In[10]:


df = np.array(X_train)
labels = np.array(Y_train)


# In[11]:


regressor = xgb.XGBRegressor(objective ='reg:linear', colsample_bytree = 1, learning_rate = 0.5, max_depth = 5, alpha = 10, n_estimators = 1000)
#regressor.fit(X_train, Y_train)

#y_pred = regressor.predict(X_test)


# In[12]:


scores=[]


# In[13]:


def get_score(model,X_train,X_test,y_train,y_test):
    model.fit(X_train,y_train)
    return abs(model.score(X_test,y_test))


# In[14]:


kf = KFold(n_splits=5)
kf.get_n_splits(X_train)


# In[15]:


KFold(n_splits=5, random_state=None, shuffle=False)
for train_index, test_index in kf.split(df):
    x_train, x_test = df[train_index], df[test_index]
    y_train, y_test = labels[train_index], labels[test_index]
    
    scores.append(get_score(regressor,x_train,x_test,y_train,y_test))
    


# In[16]:


scores


# In[17]:


sum(scores)/5


# In[18]:


y_pred = regressor.predict(np.array(X_test))


# In[19]:


from sklearn.metrics import mean_squared_error
root_mean_squared_error = mean_squared_error(Y_test, y_pred)**0.5
print("root_mean_squared_error: %f" % (root_mean_squared_error))


# In[20]:


from sklearn.utils import check_array

def percentage_error(y_true, y_pred): 
    #y_true, y_pred = check_array(y_true, y_pred)

    return (np.abs((y_true.mean() - y_pred.mean()) / y_true.mean())) * 100

x=percentage_error(Y_test, y_pred)


# In[21]:


Accuracy = 100-percentage_error(Y_test, y_pred)


# ##### 

# In[22]:


Accuracy


# In[ ]:




