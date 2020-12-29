package com.rndouyin

import android.content.Intent

public object modulelist {
  var mlist: ArrayList<RndouyinModule> = ArrayList()
  fun addlist(m: RndouyinModule) {
    mlist.add(m)
  }
  fun handleIntent(intent: Intent) {
    for(m: RndouyinModule in mlist) {
      m.handleIntent(intent)
    }
  }
}
