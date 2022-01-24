package com.gm.utilities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import com.gm.R
import com.gm.db.SingleTon
import com.gm.listener.OnBookGridTouched
import com.google.android.material.snackbar.Snackbar
import java.util.Arrays
import java.util.LinkedList
class MultiSpinnerReport:AppCompatSpinner, OnMultiChoiceClickListener {
    private var _items:Array<String>? = null
    private var _selection:BooleanArray? = null
    private var _proxyAdapter:ArrayAdapter<String>
    var mCallback:OnBookGridTouched?=null

    val selectedIndicies:List<Int>
        get() {
            val selection = LinkedList<Int>()
            for (i in _items?.indices!!)
            {
                if (this!!._selection?.get(i)!!)
                {
                    selection.add(i)
                }
            }
            return selection
        }
    constructor(context:Context) : super(context) {
        _proxyAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item)
        super.setAdapter(_proxyAdapter)
    }
    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        _proxyAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item)
        super.setAdapter(_proxyAdapter)
    }
    constructor(context:Context, attrs:OnMultiChoiceClickListener) : super(context, attrs as AttributeSet) {
        _proxyAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item)
        super.setAdapter(_proxyAdapter)
    }
    fun setSelectedLabel() {
        _proxyAdapter.add(SingleTon.getResourceStringValue("selected_batches"))
    }
    override fun onClick(dialog:DialogInterface, which:Int, isChecked:Boolean) {
        (dialog as AlertDialog).setCanceledOnTouchOutside(false)
        val tx = TextView(getContext())
        tx.setText(SingleTon.getResourceStringValue("batch_selection"))
        (dialog as AlertDialog).setCustomTitle(tx)
        if (_selection != null && which < _selection?.size!!)
        {
            if (getSelectedStrings().size < 3)
            {
                _selection!![which] = isChecked
                _proxyAdapter.clear()
                if (getSelectedStrings().size > 1)
                {
                    _proxyAdapter.add(getSelectedStrings().size.toString() + " " + SingleTon.getResourceStringValue("message_selected_batches"))
                }
                else
                {
                    _proxyAdapter.add(getSelectedStrings().size.toString() + " " + SingleTon.getResourceStringValue("message_selected_batch"))
                }
            }
            else
            {
                _selection!![which] = false
                Snackbar
                        .make((dialog as AlertDialog).getListView(),SingleTon.getResourceStringValue("message_batch_selection"), Snackbar.LENGTH_LONG)
                        .show()
                (dialog as AlertDialog).getListView().setItemChecked(which, false)
            }
        }
        else
        {
            throw IllegalArgumentException("Argument 'which' is out of bounds.")
        }
    }
    override fun performClick():Boolean {
        val builder = AlertDialog.Builder(getContext())
        val tx = TextView(getContext())
        tx.setText(SingleTon.getResourceStringValue("batch_selection"))
        tx.setPadding(40, 20, 10, 10)
        builder.setCustomTitle(tx)
        builder.setMultiChoiceItems(_items, _selection, this)
        builder.setPositiveButton(getContext().getString(R.string.submit), object:DialogInterface.OnClickListener {
           override fun onClick(dialog:DialogInterface, which:Int) {
                // _proxyAdapter.add(getContext().getString(R.string.selected_batches));
                mCallback?.onTouchGrid()
            }
        })
        /* builder.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
     @Override
     public void onClick(DialogInterface dialog, int which) {
     }
     });*/
        builder.setCancelable(false)
        builder.show()
        return true
    }
   override fun setAdapter(adapter:SpinnerAdapter) {
        throw RuntimeException("setAdapter is not supported by MultiSelectSpinner.")
    }
    fun setItems(items:Array<String>) {
        _items = items
        _selection = BooleanArray(_items!!.size)
        setSelection(0, true)
        Arrays.fill(_selection, false) // true defaults to checked, false defaults to unchecked
        _proxyAdapter.clear()
        _proxyAdapter.add(buildSelectedItemString())
    }
    fun setItemsChecked(items:Array<String>) {
        _items = items
        _selection = BooleanArray(_items!!.size)
        Arrays.fill(_selection, true) // true defaults to checked, false defaults to unchecked
        _proxyAdapter.clear()
        _proxyAdapter.add(buildSelectedItemString())
    }
    fun setItemsSaved(items:Array<String>, saved:String) {
        _items = items
        _selection = BooleanArray(_items!!.size)
        val sb = StringBuilder()
        var foundOne = false
        for (i in _items!!.indices)
        {
            _selection!![i] = saved.toLowerCase().contains(_items!![i].toLowerCase())
            if (_selection!![i])
            {
                if (foundOne)
                {
                    sb.append(", ")
                }
                foundOne = true
                sb.append(_items!![i])
            }
        }
        _proxyAdapter.clear()
        _proxyAdapter.add(buildSelectedItemString())
    }
    fun setItems(items:List<String>) {
        _items = items.toTypedArray<String>()
        _selection = BooleanArray(_items!!.size)
        Arrays.fill(_selection, false) // true defaults to checked, false defaults to unchecked
        Arrays.fill(_selection, false)
        Arrays.fill(_selection, 0, 1, true)// true defaults to checked, false defaults to unchecked
        _proxyAdapter.clear()
        _proxyAdapter.add(getSelectedStrings().size.toString() + " " +SingleTon.getResourceStringValue("message_selected_batch"))
        // _proxyAdapter.add(getContext().getString(R.string.selected_batches));
    }
    fun setItemsChecked(items:List<String>) {
        _items = items.toTypedArray<String>()
        _selection = BooleanArray(_items!!.size)
        Arrays.fill(_selection, true) // true defaults to checked, false defaults to unchecked
        _proxyAdapter.clear()
        _proxyAdapter.add(buildSelectedItemString())
    }
    fun setData() {
        //((AlertDialog) dialog).getListView().setItemChecked(which, false);
    }
    fun setCallback(callback:OnBookGridTouched) {
        mCallback = callback
    }
    fun setSelection(selection:Array<String>) {
        for (sel in selection)
        {
            for (j in _items?.indices!!)
            {
                if (_items!![j] == sel)
                {
                    _selection?.set(j, true)
                }
            }
        }
    }



    fun getSelectedStrings():List<String> {
        val selection = LinkedList<String>()
        if (_items != null)
        {
            for (i in 0 until _items?.size!!)
            {
                if (_selection?.get(i)!!)
                {
                    selection.add(_items!![i])
                }
            }
        }
        return selection
    }
    fun setSelection(selection:List<String>) {
        for (sel in selection)
        {
            for (j in _items?.indices!!)
            {
                if (_items!![j] == sel)
                {
                    _selection?.set(j, true)
                }
            }
        }
    }
    fun setSelection(selectedIndicies:IntArray) {
        for (index in selectedIndicies)
        {
            if (index >= 0 && index < _selection?.size!!)
            {
                _selection!![index] = true
            }
            else
            {
                throw IllegalArgumentException("Index " + index + " is out of bounds.")
            }
        }
    }
    private fun buildSelectedItemString():String {
        val sb = StringBuilder()
        var foundOne = false
        for (i in _items?.indices!!)
        {
            if (_selection?.get(i)!!)
            {
                if (foundOne)
                {
                    sb.append(", ")
                }
                foundOne = true
                sb.append(_items!![i])
            }
        }
        return sb.toString()
    }
    companion object {
        private val _submited:Boolean = false
    }
}